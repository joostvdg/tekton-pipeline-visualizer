/* (C)2024 */
package net.joostvdg.tektonvisualizer.harvester.parser;

import com.google.gson.JsonElement;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import net.joostvdg.tektonvisualizer.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Parses the TaskRun reference from a PipelineRun's Status. */
@Service
public class TaskRunParser implements TektonResourceParser {

  private static final Logger logger = LoggerFactory.getLogger(TaskRunParser.class);

  @Override
  public Optional<TektonResourceType> parse(DynamicKubernetesObject taskRun) {

    // TODO: implement the TaskRunReferenceParser
    // This parser should parse the TaskRun reference from a PipelineRun's Status
    // And it should return PipelineStages which contain PipelineSteps if applicable

    // fields we need to extract from the resource:
    // - kind
    // - metadata.name
    // - spec.taskRef.name
    // - status.conditions (type == Succeeded)
    if (taskRun == null) {
      return Optional.empty();
    }

    String kind = taskRun.getKind();
    // verify if the resource is a TaskRun
    if (!"TaskRun".equals(kind)) {
      return Optional.empty();
    }
    String name = taskRun.getMetadata().getName();

    JsonElement statusData = taskRun.getRaw().get("status");
    if (statusData == null) {
      logger.warn("No status data found for TaskRun: {}", name);
      return Optional.empty();
    }

    JsonElement conditions = statusData.getAsJsonObject().get("conditions");
    JsonElement firstCondition = conditions.getAsJsonArray().get(0);
    // TODO: filter on type == Succeeded, for now, assume it is the first condition (and only)
    // TODO: extract condition parsing to a separate method
    String message = firstCondition.getAsJsonObject().get("message").getAsString();
    String reason = firstCondition.getAsJsonObject().get("reason").getAsString();
    String statusSuccess = firstCondition.getAsJsonObject().get("status").getAsString();
    String statusType = firstCondition.getAsJsonObject().get("type").getAsString();
    Duration duration = Duration.ofSeconds(30L);
    List<PipelineStep> steps = List.of();

    boolean success = statusSuccess.equals("True");
    String explanation = reason + " - " + message;
    ExecutionStatus executionStatus = ExecutionStatus.SUCCEEDED;
    // TODO: update to reflect other possible statuses
    if (!statusType.equals("Succeeded")) {
      executionStatus = ExecutionStatus.FAILED;
    }
    Status status = new Status(success, explanation, executionStatus);
    PipelineStage stage = new PipelineStage(name, name, status, duration, 0, steps);

    return Optional.of(stage);
  }

  @Override
  public List<TektonResourceType> parseList(DynamicKubernetesObject resource) {
    return List.of();
  }
}
