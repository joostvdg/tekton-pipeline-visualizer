/* (C)2024 */
package net.joostvdg.tektonvisualizer.serializer;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.joostvdg.tektonvisualizer.model.*;
import net.joostvdg.tektonvisualizer.serialize.JsonSerializer;
import org.junit.jupiter.api.Test;

public class JsonSerializerTest {

  private String pipelineStageJson =
      """
    {"duration":"PT10S","identifier":"a5a35905-0a7d-490c-92ce-1c244442c3fd","name":"stage-1","order":1,"status":{"executionStatus":"SUCCEEDED","explanation":"SUCCEEDED","success":true},"steps":[]}
    """;

  private String pipelineStatusJson =
      """
{"duration":"PT10S","identifier":"44121a86-85b7-4505-b4ab-c177b7c23dea","instantOfCompletion":"1970-01-01T00:00:10Z","instantOfStart":"1970-01-01T00:00:00Z","name":"pipeline-1","pipelineIdentifier":"pipelineId","results":{"result-1":"value-1"},"stages":[],"status":{"executionStatus":"SUCCEEDED","explanation":"Failed - Tasks Completed: 3 (Failed: 2, Cancelled 0), Skipped: 8","success":true},"trigger":{"eventId":"b09a980e-2b5d-4321-afab-068ac1d2f45d","eventListener":"idec-image-builds","rerun":false,"rerunOf":"","trigger":"github-listener","triggerType":"GitHub"}}
""";

  @Test
  public void testToJsonPipelineStage() {
    Status status = new Status(true, "SUCCEEDED", ExecutionStatus.SUCCEEDED);
    PipelineStage pipelineStage =
        new PipelineStage.Builder()
            .identifier("a5a35905-0a7d-490c-92ce-1c244442c3fd")
            .name("stage-1")
            .status(status)
            .duration(Duration.ofSeconds(10))
            .order(1)
            .steps(new ArrayList<>())
            .build();
    TektonResourceType tektonResourceType = pipelineStage;

    String json = JsonSerializer.toJson(tektonResourceType);
    assertNotNull(json);
    assertEquals(pipelineStageJson.trim(), json.trim());
  }

  @Test
  public void testPipelineStageFromJson() {
    TektonResourceType tektonResourceType =
        JsonSerializer.fromJson(pipelineStageJson, PipelineStage.class);
    assertNotNull(tektonResourceType);
    assertTrue(tektonResourceType instanceof PipelineStage);
    PipelineStage pipelineStage = (PipelineStage) tektonResourceType;
    assertEquals("a5a35905-0a7d-490c-92ce-1c244442c3fd", pipelineStage.identifier());
    assertEquals("stage-1", pipelineStage.name());
    assertEquals(1, pipelineStage.order());
    assertEquals(Duration.ofSeconds(10), pipelineStage.duration());
    assertEquals(0, pipelineStage.steps().size());
    Status status = pipelineStage.status();
    assertNotNull(status);
    assertTrue(status.success());
    assertEquals("SUCCEEDED", status.explanation());
    assertEquals(ExecutionStatus.SUCCEEDED, status.executionStatus());
  }

  @Test
  public void testPipelineStatusToJson() {
    Status status =
        new Status(
            true,
            "Failed - Tasks Completed: 3 (Failed: 2, Cancelled 0), Skipped: 8",
            ExecutionStatus.SUCCEEDED);
    Instant start = Instant.ofEpochSecond(0);
    Instant completion = Instant.ofEpochSecond(10);
    Duration duration = Duration.ofSeconds(10);
    PipelineTrigger pipelineTrigger =
        new PipelineTrigger(
            TriggerType.GitHub,
            "github-listener",
            "b09a980e-2b5d-4321-afab-068ac1d2f45d",
            false,
            "",
            "idec-image-builds");
    Map<String, String> results = new HashMap<>();
    results.put("result-1", "value-1");
    PipelineStatus pipelineStatus =
        new PipelineStatus.Builder()
            .identifier("44121a86-85b7-4505-b4ab-c177b7c23dea")
            .pipelineIdentifier("pipelineId")
            .name("pipeline-1")
            .status(status)
            .stages(new ArrayList<>())
            .instantOfCompletion(completion)
            .instantOfStart(start)
            .duration(duration)
            .trigger(pipelineTrigger)
            .results(results)
            .build();
    TektonResourceType tektonResourceType = pipelineStatus;

    String json = JsonSerializer.toJson(tektonResourceType);
    assertNotNull(json);
    assertEquals(pipelineStatusJson.trim(), json.trim());
  }

  @Test
  public void testPipelineStatusFromJson() {
    TektonResourceType tektonResourceType =
        JsonSerializer.fromJson(pipelineStatusJson, PipelineStatus.class);
    assertNotNull(tektonResourceType);
    assertTrue(tektonResourceType instanceof PipelineStatus);
    PipelineStatus pipelineStatus = (PipelineStatus) tektonResourceType;
    assertEquals("44121a86-85b7-4505-b4ab-c177b7c23dea", pipelineStatus.identifier());
    assertEquals("pipelineId", pipelineStatus.pipelineIdentifier());
    assertEquals("pipeline-1", pipelineStatus.name());
    assertEquals(0, pipelineStatus.stages().size());
    assertEquals(Instant.ofEpochSecond(0), pipelineStatus.instantOfStart());
    assertEquals(Instant.ofEpochSecond(10), pipelineStatus.instantOfCompletion());
    assertEquals(Duration.ofSeconds(10), pipelineStatus.duration());
    PipelineTrigger trigger = pipelineStatus.trigger();
    assertNotNull(trigger);
    assertEquals("b09a980e-2b5d-4321-afab-068ac1d2f45d", trigger.eventId());
    assertEquals("idec-image-builds", trigger.eventListener());
    assertFalse(trigger.rerun());
    assertEquals("", trigger.rerunOf());
    assertEquals("github-listener", trigger.trigger());
    assertEquals(TriggerType.GitHub, trigger.triggerType());
    Map<String, String> results = pipelineStatus.results();
    assertNotNull(results);
    assertEquals(1, results.size());
    assertEquals("value-1", results.get("result-1"));
    Status status = pipelineStatus.status();
    assertNotNull(status);
    assertTrue(status.success());
    assertEquals(
        "Failed - Tasks Completed: 3 (Failed: 2, Cancelled 0), Skipped: 8", status.explanation());
    assertEquals(ExecutionStatus.SUCCEEDED, status.executionStatus());
  }
}
