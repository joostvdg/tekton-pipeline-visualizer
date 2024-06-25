/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.pipeline;

import static net.joostvdg.tektonvisualizer.notifier.AppConfig.PIPELINE_STATUS_TOPIC;

import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.notifier.NotifierApplication;
import net.joostvdg.tektonvisualizer.serialize.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
  // TODO: map incoming message to notification settings
  // TODO: example, if "watched" and "success" then send notification according to config
  // TODO: config can be, another Kafka topic, slack, discord, BlueSkye, etc.
  // TODO: config can also be, webhook internally (e.g., Tekton EventListener) with specific payload

  private final Logger logger = LoggerFactory.getLogger(NotifierApplication.class);

  private final PipelineProcessor pipelineProcessor;

  public Receiver(PipelineProcessor pipelineProcessor) {
    this.pipelineProcessor = pipelineProcessor;
  }

  @KafkaListener(id = "notifier", topics = PIPELINE_STATUS_TOPIC)
  public void listenForPipelineStatusTopic(String pipelineStatusJson) {
    logger.info("Received message, size: {}", pipelineStatusJson.length());

    // TODO: parse message into pipeline status
    // parse result type, result status, source (e.g., git repo/path) and see if we have a config
    // match
    // if we have a config match, send to Routing component

    // TODO: implement
    PipelineStatus pipelineStatus =
        JsonSerializer.fromJson(pipelineStatusJson, PipelineStatus.class);
    if (pipelineStatus != null) {
      logger.info("Parsed message: {}", pipelineStatus);
      pipelineProcessor.process(pipelineStatus);
    } else {
      logger.error("Failed to parse message: {}", pipelineStatusJson);
      return;
    }
  }
}
