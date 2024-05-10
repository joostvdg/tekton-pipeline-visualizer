package net.joostvdg.tektonvisualizer.harvester.model;

import com.google.gson.JsonElement;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public record PipelineStatus(String name, Status lastRecordedStatus, List<Status> previousStatuses) {

    private static final Logger logger = LoggerFactory.getLogger(PipelineStatus.class);
    private static final String PIPELINE_RUN_KIND = "PipelineRun";

  public static Optional<PipelineStatus> from(DynamicKubernetesObject pipelineRun) {

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
      String statusType = firstCondition.getAsJsonObject().get("status").getAsString();

      var startTime = parseDateTime(startTimeData.getAsString());
      var completionTime = parseDateTime(completionTimeData.getAsString());
      var duration = Duration.between(startTime, completionTime);
      var statusMessage = message + " (" + reason + ")";
      Status status =
      new Status(statusType.equals("True"), duration, startTime, completionTime, statusMessage);

      logger.debug("Found PipelineRun: {} in namespace: {} with status: {}", name, namespace, status);
      return Optional.of(new PipelineStatus(name, status, List.of()));
    }

    private static LocalDateTime parseDateTime(String asString) {
      // 2024-02-25T23:29:50Z
      Instant instant = Instant.parse(asString);
      return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
