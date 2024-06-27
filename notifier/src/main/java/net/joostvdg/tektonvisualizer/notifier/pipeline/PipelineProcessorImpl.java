/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.pipeline;

import java.util.*;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.notifier.mapping.EventStatus;
import net.joostvdg.tektonvisualizer.notifier.mapping.NotificationMapping;
import net.joostvdg.tektonvisualizer.notifier.mapping.NotificationMappings;
import net.joostvdg.tektonvisualizer.notifier.mapping.SourceType;
import net.joostvdg.tektonvisualizer.notifier.routing.WebhookRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PipelineProcessorImpl implements PipelineProcessor {

  // TODO: capture pipeline references, so we can compare current vs. previous state

  private final Logger logger = LoggerFactory.getLogger(PipelineProcessorImpl.class);

  private final NotificationMappings notificationMappings;

  private final Map<String, PipelineStatus> pipelineStatusProcessed;

  private final Set<String> pipelineStatusDiscarded;

  // TODO: abstract away via a messaging service (Spring Modulith?
  private final WebhookRouter webhookRouter;

  // TODO: store processed pipeline statuses, so we can compare current vs. previous state
  // what we need: pipeline status (success, failure, etc), git repo, subPath, result type, and instantOfCompletion
  // when we receive a new status, we find matching previous statuses, and compare the instantOfCompletion
  // if the new status is older, we discard it
  // if the new status is newer, we process it and set the previous status to the one with the latest instantOfCompletion

  public PipelineProcessorImpl(
      NotificationMappings notificationMappings, WebhookRouter webhookRouter) {
    this.notificationMappings = notificationMappings;
    this.webhookRouter = webhookRouter;
    this.pipelineStatusProcessed = new HashMap<>();
    this.pipelineStatusDiscarded = new HashSet<>();
  }

  @Override
  public void process(PipelineStatus pipelineStatus) {
    if (pipelineStatusProcessed.containsKey(pipelineStatus.name())) {
      logger.info("Pipeline status already processed: {}", pipelineStatus.name());
      return;
    }

    NotificationMapping mapping = null;
    if (getMapping(pipelineStatus).isPresent()) {
      logger.info("Mapping found for pipeline status: {}", pipelineStatus.name());
      pipelineStatusProcessed.put(pipelineStatus.name(), pipelineStatus);
      mapping = getMapping(pipelineStatus).get();
    } else {
      logger.info("No mapping found for pipeline status: {}", pipelineStatus.name());
      pipelineStatusDiscarded.add(pipelineStatus.name());
      return;
    }

    if (mapping.enabled()) {
      logger.info("Mapping for pipeline {} is enabled, routing to webhook", pipelineStatus.name());
      webhookRouter.route(mapping, pipelineStatus);
    }
  }

  /**
   * Check if we have a mapping for the given pipeline status.
   *
   * <p>The mapping is on the Source (Git Repository and subPath), and the Result Type.
   *
   * @param pipelineStatus the pipeline status to check
   * @return the mapping if found
   */
  @Override
  public Optional<NotificationMapping> getMapping(PipelineStatus pipelineStatus) {
    var results = pipelineStatus.results();
    if (results == null || results.isEmpty()) {
      return Optional.empty();
    }
    String resultType = results.get("RESULT_TYPE");
    String repoUrl = results.get("REPO_URL");
    String repoPath = results.get("REPO_PATH");
    if (repoPath == null || repoPath.isEmpty()) {
      repoPath = ".";
    }

    if (resultType == null || repoUrl == null) {
      return Optional.empty();
    }

    // TODO: handle different event statuses
    var eventStatus = EventStatus.SUCCESS;
    if (!pipelineStatus.status().success()) {
      eventStatus = EventStatus.FAILURE;
    }

    for (var mapping : notificationMappings.mappings()) {

      if (mapping.source().gitUrl().equals(repoUrl)
          && mapping.source().subPath().equals(repoPath)
          && mapping.source().type().equals(SourceType.fromString(resultType))
          && mapping.target().when().current().equals(eventStatus)) {
        logger.info("Found mapping {} for pipeline status: {}", mapping, pipelineStatus.name());
        return Optional.of(mapping);
      }
    }

    return Optional.empty();
  }
}
