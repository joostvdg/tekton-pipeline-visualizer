/* (C)2024 */
package net.joostvdg.tektonvisualizer.harvester.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Optional;

import net.joostvdg.tektonvisualizer.harvester.parser.TaskRunParser;
import net.joostvdg.tektonvisualizer.model.TektonResourceType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.boot.test.context.SpringBootTest
public class TaskRunParserTest {

  private static final String RESOURCE_PATH = "src/test/resources";
  private static final String TEKTON_TASK_RUN_JSON = "tekton-task-run.json";

    @Autowired
    private TaskRunParser taskRunParser;

  @Test
  void testParse() {
    var expectedName = "idec-image-builds-run-tqzgl-r-sl49z-fetch-source";
    var loadedTaskRun = loadTaskRun();
    assertEquals("taskrun", loadedTaskRun.getKind().toLowerCase(Locale.ROOT));
    assertEquals(expectedName, loadedTaskRun.getMetadata().getName());

    // TODO finish test
     Optional<TektonResourceType> pipelineStagesOpt = taskRunParser.parse(loadedTaskRun);
     assertTrue(pipelineStagesOpt.isPresent());

  }

  private DynamicKubernetesObject loadTaskRun() {
    // build a mock for the DynamicObject that should represent the PipelineRun
    // load the PipelineRun from the file: tekton-pipeline-run.yaml
    var testFilePipelineRun = new File(RESOURCE_PATH + File.separator + TEKTON_TASK_RUN_JSON);
    assertTrue(testFilePipelineRun.exists());

    try {
      String jsonString = Files.readString(testFilePipelineRun.toPath());
      JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
      return new DynamicKubernetesObject(jsonObject);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
