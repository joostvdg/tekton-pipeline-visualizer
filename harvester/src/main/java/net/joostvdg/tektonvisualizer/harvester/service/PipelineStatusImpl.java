/* (C)2024 */
package net.joostvdg.tektonvisualizer.harvester.service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesApi;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;
import io.kubernetes.client.util.generic.options.ListOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import net.joostvdg.tektonvisualizer.harvester.messaging.QueueSender;
import net.joostvdg.tektonvisualizer.harvester.parser.TaskRunParser;
import net.joostvdg.tektonvisualizer.harvester.parser.TektonResourceParser;
import net.joostvdg.tektonvisualizer.model.PipelineStage;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.model.TektonResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PipelineStatusImpl implements PipelineStatusService {

  private final ApiClient apiClient;

  private final TektonResourceParser pipelineStatusParser;

  private final QueueSender queueSender;

  private final Logger logger = LoggerFactory.getLogger(PipelineStatusImpl.class);
  private final TaskRunParser taskRunParser;

  private HashMap<String, PipelineStatus> knownPipelineStatuses;

  public PipelineStatusImpl(
      ApiClient apiClient,
      TektonResourceParser pipelineStatusParser,
      TaskRunParser taskRunParser,
      QueueSender queueSender) {
    this.apiClient = apiClient;
    this.pipelineStatusParser = pipelineStatusParser;
    this.taskRunParser = taskRunParser;
    this.queueSender = queueSender;
  }

  @PostConstruct
  private void initialize() {
    if (logger.isInfoEnabled()) {
      logger.info("Initializing %s bean.".formatted(this.getClass().getName()));
    }
    knownPipelineStatuses = new HashMap<>();
  }

  @Override
  public ResponseEntity<List<PipelineStatus>> getPipelineStatuses() {
    inspectCluster();
    var results = knownPipelineStatuses.values().stream().toList();
    return ResponseEntity.ok(results);
  }

  private void inspectCluster() {
    // TODO: optimize by retrieving a list of name and only retrieve the ones that are not yet known
    // TODO: store the ones that are known in a cache
    DynamicKubernetesApi dynamicApi =
        new DynamicKubernetesApi("tekton.dev", "v1", "pipelineruns", apiClient);

    ListOptions listOptions = new ListOptions();
    listOptions.setLimit(1000);

    var pipelineRuns = dynamicApi.list("idec", listOptions).getObject();
    if (pipelineRuns == null) {
      logger.warn("No pipelineRuns found (NULL.)");
      return;
    } else if (pipelineRuns.getItems().isEmpty()) {
      logger.info("No pipelineRuns found (EMPTY.)");
    } else {
      logger.info("Found {} pipelineRuns.", pipelineRuns.getItems().size());
    }

    for (var pipelineRun : pipelineRuns.getItems()) {
      var runName = pipelineRun.getMetadata().getName();
      if (knownPipelineStatuses.containsKey(runName)) {
        logger.debug("PipelineRun: {} already known.", runName);
        continue;
      }
      Optional<TektonResourceType> pipelineStatus = pipelineStatusParser.parse(pipelineRun);
      if (pipelineStatus.isEmpty()) {
        logger.warn("Could not parse PipelineRun: {}", runName);
        continue;
      }
      var pipelineStatusObj = (PipelineStatus) pipelineStatus.get();
      knownPipelineStatuses.put(runName, pipelineStatusObj);

      // TODO: we can collect the order via the status.childReferences
      List<PipelineStage> pipelineStages = new ArrayList<>();
      var taskRuns = getTaskRunsForPipelineRun(runName);
      for (var taskRun : taskRuns) {
        var pipelineStage = taskRunParser.parse(taskRun);
        if (pipelineStage.isEmpty()) {
          logger.warn("Could not parse TaskRun: {}", taskRun.getMetadata().getName());
          continue;
        }
        pipelineStages.add((PipelineStage) pipelineStage.get());
      }
      pipelineStatusObj.stages().addAll(pipelineStages);
    }

    logger.info("Sending Pipeline Statuses to the queue.");
    PipelineStatus pipelineStatusToSend =
        knownPipelineStatuses.values().stream().findFirst().orElseThrow();
    queueSender.sendPipelineStatus(pipelineStatusToSend);
  }

  public List<DynamicKubernetesObject> getTaskRunsForPipelineRun(String pipelineRunName) {
    DynamicKubernetesApi dynamicApi =
        new DynamicKubernetesApi("tekton.dev", "v1", "taskruns", apiClient);
    ListOptions listOptions = new ListOptions();
    listOptions.setLabelSelector("tekton.dev/pipelineRun=" + pipelineRunName);
    // Attempt to limit fields retrieved. Note: This might not work as expected for nested fields.
    listOptions.setFieldSelector("metadata,spec.taskRef,status.conditions");

    try {
      var taskRuns = dynamicApi.list("default", listOptions).getObject();
      if (taskRuns != null && !taskRuns.getItems().isEmpty()) {
        return taskRuns.getItems();
      }
    } catch (Exception e) {
      logger.error("Could not retrieve TaskRuns for PipelineRun: {}", pipelineRunName, e);
    }
    return List.of();
  }
}
