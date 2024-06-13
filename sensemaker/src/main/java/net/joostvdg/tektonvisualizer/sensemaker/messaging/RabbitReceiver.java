/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.messaging;

import static net.joostvdg.tektonvisualizer.sensemaker.service.SupplyChainService.RESULT_FIELD_REPO_URL;

import java.util.concurrent.CountDownLatch;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.sensemaker.service.PipelineStatusService;
import net.joostvdg.tektonvisualizer.sensemaker.service.SupplyChainService;
import net.joostvdg.tektonvisualizer.serialize.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RabbitReceiver {

  private final Logger logger = LoggerFactory.getLogger(RabbitReceiver.class);

  private final CountDownLatch latch = new CountDownLatch(1);

  private final PipelineStatusService pipelineStatusService;

  private final SupplyChainService supplyChainService;

  public RabbitReceiver(
      PipelineStatusService pipelineStatusService, SupplyChainService supplyChainService) {
    this.pipelineStatusService = pipelineStatusService;
    this.supplyChainService = supplyChainService;
  }

  public void receiveMessage(String pipelineStatusJson) {
    PipelineStatus pipelineStatus =
        JsonSerializer.fromJson(pipelineStatusJson, PipelineStatus.class);
    logger.info("Received <{}>", pipelineStatus);
    if (pipelineStatus == null) {
      throw new IllegalArgumentException("PipelineStatus cannot be null");
    }
    var result = pipelineStatusService.newPipelineStatus(pipelineStatus);

    logger.info("PipelineStatus inserted: {}", result.success());

    if (result.success() || result.newRecordId().isPresent()) {
      var newRecordId = result.newRecordId().get();
      logger.info("PipelineStatus inserted with id: {}", newRecordId);

      var sourceUrl = "";
      // TODO: move this to PipelineStatusService
      if (pipelineStatus.results().containsKey(RESULT_FIELD_REPO_URL)) {
        sourceUrl = pipelineStatus.results().get(RESULT_FIELD_REPO_URL);
      }

      var isAttached = supplyChainService.attachPipelineStatusToSupplyChain(newRecordId, sourceUrl);
      logger.info("PipelineStatus attached to SupplyChain: {}", isAttached);
    }

    latch.countDown();
  }

  public CountDownLatch getLatch() {
    return latch;
  }
}
