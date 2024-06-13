/* (C)2024 */
package net.joostvdg.tektonvisualizer.harvester.parser;

import com.google.gson.JsonElement;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import net.joostvdg.tektonvisualizer.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PipelineStatusParser implements TektonResourceParser {

  private static final Logger logger = LoggerFactory.getLogger(PipelineStatusParser.class);
  private static final String PIPELINE_RUN_KIND = "PipelineRun";

  @Override
  public Optional<TektonResourceType> parse(DynamicKubernetesObject pipelineRun) {
    if (pipelineRun == null) {
      logger.warn("No pipelineRun found (NULL.)");
      return Optional.empty();
    } else if (pipelineRun.getKind() == null || !pipelineRun.getKind().equals(PIPELINE_RUN_KIND)) {
      logger.info("Not a pipelineRun found");
      return Optional.empty();
    }

    String name = pipelineRun.getMetadata().getName();
    String namespace = pipelineRun.getMetadata().getNamespace();
    JsonElement statusData = pipelineRun.getRaw().get("status");
    if (statusData == null) {
      logger.warn("No status data found for PipelineRun: {} in namespace: {}", name, namespace);
      return Optional.empty();
    }
    JsonElement completionTimeData = statusData.getAsJsonObject().get("completionTime");
    JsonElement startTimeData = statusData.getAsJsonObject().get("startTime");
    JsonElement conditions = statusData.getAsJsonObject().get("conditions");
    JsonElement firstCondition = conditions.getAsJsonArray().get(0);

    // TODO: filter on type == Succeeded, for now, assume it is the first condition (and only)
    String message = firstCondition.getAsJsonObject().get("message").getAsString();
    String reason = firstCondition.getAsJsonObject().get("reason").getAsString();
    String statusSuccess = firstCondition.getAsJsonObject().get("status").getAsString();
    String statusType = firstCondition.getAsJsonObject().get("type").getAsString();

    var start = Instant.parse(startTimeData.getAsString());
    var completion = Instant.parse(completionTimeData.getAsString());
    var duration = Duration.between(start, completion);

    boolean success = statusSuccess.equals("True");
    String explanation = reason + " - " + message;
    ExecutionStatus executionStatus = ExecutionStatus.SUCCEEDED;
    // TODO: update to reflect other possible statuses
    if (!statusType.equals("Succeeded")) {
      executionStatus = ExecutionStatus.FAILED;
    }
    Status status = new Status(success, explanation, executionStatus);

    logger.debug("Found PipelineRun: {} in namespace: {} with status: {}", name, namespace, status);
    // TODO: collect the TaskRun references from the PipelineRun's Status and store them in the

    List<PipelineStage> pipelineStages = new ArrayList<>();
    if (statusData.getAsJsonObject() != null && statusData.getAsJsonObject().get("childReferences") != null) {
      var childReferences = statusData.getAsJsonObject().get("childReferences").getAsJsonArray();
      if (childReferences == null || childReferences.isEmpty()) {
        logger.warn(
                "No child references found for PipelineRun: {} in namespace: {}", name, namespace);
      } else {
        int order = 0;
        for (JsonElement childReference : childReferences) {
          String taskRunId = childReference.getAsJsonObject().get("name").getAsString();
          String taskName = childReference.getAsJsonObject().get("pipelineTaskName").getAsString();
          var pipelineStage =
                  new PipelineStage(
                          taskRunId,
                          taskName,
                          new Status(true, "N/A", ExecutionStatus.SUCCEEDED),
                          Duration.ofSeconds(0),
                          order++,
                          List.of());
          pipelineStages.add(pipelineStage);
        }
      }
    }


    // TODO: parse result
    var results = parseResults(statusData.getAsJsonObject().get("results"));

    // TODO also create PipelineStage for each Skipped Task

    String rerunOf =
        pipelineRun.getMetadata().getLabels().getOrDefault("dashboard.tekton.dev/rerunOf", "");
    String eventListener =
        pipelineRun.getMetadata().getLabels().getOrDefault("triggers.tekton.dev/eventlistener", "");
    String trigger =
        pipelineRun.getMetadata().getLabels().getOrDefault("triggers.tekton.dev/trigger", "");
    String triggersEventId =
        pipelineRun
            .getMetadata()
            .getLabels()
            .getOrDefault("triggers.tekton.dev/triggers-eventid", "");
    boolean rerun = !rerunOf.isEmpty();

    // Assuming TriggerInfo is a class that holds the trigger information
    // TriggerInfo triggerInfo = new TriggerInfo(rerunOf, pipeline, eventListener, trigger,
    // triggersEventId);
    PipelineTrigger triggerInfo =
        new PipelineTrigger(
            TriggerType.GitHub, trigger, triggersEventId, rerun, rerunOf, eventListener);
    logger.debug("Collected Trigger Information: {}", triggerInfo);

    PipelineStatus pipelineStatus =
        new PipelineStatus.Builder()
            .pipelineIdentifier("pipelineId")
            .name(name)
            .status(status)
            .stages(pipelineStages)
            .instantOfCompletion(completion)
            .instantOfStart(start)
            .duration(duration)
            .trigger(triggerInfo)
            .results(results)
            .build();

    return Optional.of(pipelineStatus);
  }

  private Map<String, String> parseResults(JsonElement resultsMap) {
    // these are KeyValue pairs
    // { "name": "IMAGE_URL",
    //   value": "harbor.home.lab/homelab/idec:0.2.5"
    // }
    var results = new HashMap<String, String>();
    if (resultsMap == null) {
      return results;
    }
    for (JsonElement result : resultsMap.getAsJsonArray()) {
      String name = result.getAsJsonObject().get("name").getAsString();
      String value = result.getAsJsonObject().get("value").getAsString();
      results.put(name, value);
    }

    return results;
  }

  @Override
  public List<TektonResourceType> parseList(DynamicKubernetesObject resource) {
    // TODO: implement
    return List.of();
  }
}
