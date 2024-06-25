package net.joostvdg.tektonvisualizer.sensemaker.messaging;

import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.serialize.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static net.joostvdg.tektonvisualizer.sensemaker.SensemakerApplication.PIPELINE_STATUS_TOPIC;

@Component
public class KafkaSender {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Logger logger = LoggerFactory.getLogger(KafkaSender.class);

    public KafkaSender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(PipelineStatus pipelineStatus) throws InterruptedException {
        var sendResultCompletableFuture = kafkaTemplate.send(PIPELINE_STATUS_TOPIC, JsonSerializer.toJson(pipelineStatus));
        try {
            var result = sendResultCompletableFuture.get(180, TimeUnit.SECONDS);
            logger.info("Sent PipelineStatus to Kafka: {}", result);
        } catch (ExecutionException e) {
            logger.error("Error while waiting for Kafka response", e);
        } catch (TimeoutException e) {
            logger.error("Timeout while waiting for Kafka response", e);
        }
    }
}
