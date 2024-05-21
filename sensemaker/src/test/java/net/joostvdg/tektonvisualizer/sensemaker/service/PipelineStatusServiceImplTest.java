/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import net.joostvdg.tektonvisualizer.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
@Transactional
class PipelineStatusServiceImplTest {

  static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

  static {
    postgres.start();
  }

  @Autowired private PipelineStatusServiceImpl pipelineStatusServiceImpl;

  @Test
  void shouldFindTestPipelineStatus() {
    List<PipelineStatus> pipelineStatuses = pipelineStatusServiceImpl.getAllPipelineStatuses();
    assertFalse(pipelineStatuses.isEmpty());
    assertEquals(1, pipelineStatuses.size());
    PipelineStatus pipelineStatus = pipelineStatuses.get(0);
    assertEquals(2, pipelineStatus.stages().size());
    assertEquals("Pipeline Status Name", pipelineStatus.name());
    assertEquals(3, pipelineStatus.results().size());
    assertEquals("github-webhook-listener", pipelineStatus.trigger().eventListener());
  }

  @Test
  void shouldInsertNewPipelineStatus() {
    var status = new Status(true, "Success", ExecutionStatus.SUCCEEDED);
    var trigger = new PipelineTrigger(TriggerType.GitHub, "Github Webhook Listener", "12345", false, "", "myListener");
    var pipelineStatus =
        new PipelineStatus.Builder()
            .name("New Pipeline Status")
            .pipelineIdentifier("new-pipeline-identifier")
            .instantOfStart(Instant.now())
            .instantOfCompletion(Instant.now())
            .results(new HashMap<>())
            .stages(List.of())
            .status(status)
            .trigger(trigger)
            .build();
    Integer newId = pipelineStatusServiceImpl.newPipelineStatus(pipelineStatus);
    assertNotNull(newId);
    assertTrue( newId > 0);

    var numOfPipelineStatuses = pipelineStatusServiceImpl.getAllPipelineStatuses().size();
    assertEquals(2, numOfPipelineStatuses);
  }
  

}
