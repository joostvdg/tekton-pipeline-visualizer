/* (C)2024 */
package net.joostvdg.tektonvisualizer.harvester.parser;

import com.google.gson.JsonElement;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import net.joostvdg.tektonvisualizer.model.ExecutionStatus;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.model.Status;
import net.joostvdg.tektonvisualizer.model.TektonResourceType;
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
    // TODO: add results and stages

    // TODO: Parse Stages from PipelineRun.status.childReferences -> TaskRuns
    PipelineStatus pipelineStatus =
        new PipelineStatus.Builder()
            .pipelineIdentifier("pipelineId")
            .name(name)
            .status(status)
            .instantOfCompletion(completion)
            .instantOfStart(start)
            .duration(duration)
            .build();

    return Optional.of(pipelineStatus);
  }
}
