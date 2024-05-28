/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import static net.joostvdg.tektonvisualizer.sensemaker.service.SupplyChainService.RESULT_FIELD_REPO_URL;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import net.joostvdg.tektonvisualizer.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class SupplyChainServiceImplTest {

  @Autowired private SupplyChainServiceImpl supplyChainServiceImpl;
  @Autowired private PipelineStatusServiceImpl pipelineStatusServiceImpl;

  @Test
  void shouldFindAllSupplyChainss() {
    List<SupplyChain> supplyChains = supplyChainServiceImpl.getAllSupplyChains();
    assertFalse(supplyChains.isEmpty());
    assertEquals(1, supplyChains.size());
  }

  @Test
  void shouldAttachPipelineStatusToSupplyChain() {
    // create new PipelineStatus, no mocks
    HashMap<String, String> results = new HashMap<>();
    results.put(
        RESULT_FIELD_REPO_URL, "https://github.com/joostvdg/tekton-pipeline-visualizer.git");
    var status = new Status(true, "Success", ExecutionStatus.SUCCEEDED);
    var trigger =
        new PipelineTrigger(
            TriggerType.GitHub, "Github Webhook Listener", "12345", false, "", "myListener");
    var pipelineStatus =
        new PipelineStatus.Builder()
            .name("New Pipeline Status")
            .pipelineIdentifier("new-pipeline-identifier")
            .instantOfStart(Instant.now())
            .instantOfCompletion(Instant.now())
            .results(results)
            .stages(List.of())
            .status(status)
            .trigger(trigger)
            .build();
    Integer newId = pipelineStatusServiceImpl.newPipelineStatus(pipelineStatus);
    var pipelineStatusSaved =
        new PipelineStatus.Builder()
            .identifier(newId.toString())
            .name("New Pipeline Status")
            .pipelineIdentifier("new-pipeline-identifier")
            .instantOfStart(Instant.now())
            .instantOfCompletion(Instant.now())
            .results(results)
            .stages(List.of())
            .status(status)
            .trigger(trigger)
            .build();
    boolean attached =
        supplyChainServiceImpl.attachPipelineStatusToSupplyChain(pipelineStatusSaved);
    assertTrue(attached);
  }
}
