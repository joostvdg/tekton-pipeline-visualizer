/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.messaging;

import java.util.concurrent.CountDownLatch;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.sensemaker.service.PipelineStatusService;
import net.joostvdg.tektonvisualizer.serialize.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RabbitReceiver {

  private final Logger logger = LoggerFactory.getLogger(RabbitReceiver.class);

  private final CountDownLatch latch = new CountDownLatch(1);

  private final PipelineStatusService pipelineStatusService;

  public RabbitReceiver(PipelineStatusService pipelineStatusService) {
    this.pipelineStatusService = pipelineStatusService;
  }

  public void receiveMessage(String pipelineStatusJson) {
    PipelineStatus pipelineStatus =
        JsonSerializer.fromJson(pipelineStatusJson, PipelineStatus.class);
    logger.info("Received <{}>", pipelineStatus);
    if (pipelineStatus == null) {
      throw new IllegalArgumentException("PipelineStatus cannot be null");
    }
    var isInserted = pipelineStatusService.newPipelineStatus(pipelineStatus);
    logger.info("PipelineStatus inserted: {}", isInserted);
    latch.countDown();
  }

  public CountDownLatch getLatch() {
    return latch;
  }
}
