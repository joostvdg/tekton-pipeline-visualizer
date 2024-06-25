package net.joostvdg.tektonvisualizer.sensemaker.service;

import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.sensemaker.messaging.KafkaSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final KafkaSender kafkaSender;

    public NotificationServiceImpl(KafkaSender kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    @Override
    public void notifyOnWatchedPipelineStatusReceived(PipelineStatus pipelineStatus) {
        logger.info("Notifying on watched PipelineStatus: {}", pipelineStatus);

        try {
            kafkaSender.send(pipelineStatus);
        } catch (InterruptedException e) {
            // TODO: what to do here? Sonar says we can't just leave it, not sure.
            logger.error("Error while sending PipelineStatus to Kafka", e);
        }
    }
}
