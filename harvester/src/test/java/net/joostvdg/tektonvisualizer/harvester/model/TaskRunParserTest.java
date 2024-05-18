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
import org.junit.jupiter.api.Test;

@org.springframework.boot.test.context.SpringBootTest
public class TaskRunReferenceParserTest {
  private static final String RESOURCE_PATH = "src/test/resources";
  private static final String TEKTON_PIPELINE_RUN_JSON = "tekton-pipeline-run.json";

  //    @Autowired
  //    private TaskRunReferenceParser taskRunReferenceParser;

  @Test
  void testParse() {
    var expectedPipelineName = "idec-image-builds-run-w94kx";
    var testPipelineStatus = loadPipelineRun();
    assertEquals("pipelinerun", testPipelineStatus.getKind().toLowerCase(Locale.ROOT));
    assertEquals(expectedPipelineName, testPipelineStatus.getMetadata().getName());

    // TODO finish test
    // Optional<TektonResourceType> pipelineStagesOpt =
    // taskRunReferenceParser.parse(testPipelineStatus);
    // assertTrue(pipelineStagesOpt.isPresent());
    //        Pipe pipelineStatus = (PipelineStatus) pipelineStatusOpt.get();
    //        assertEquals(expectedPipelineName, pipelineStatus.name());
  }

  private DynamicKubernetesObject loadPipelineRun() {
    // build a mock for the DynamicObject that should represent the PipelineRun
    // load the PipelineRun from the file: tekton-pipeline-run.yaml
    var testFilePipelineRun = new File(RESOURCE_PATH + File.separator + TEKTON_PIPELINE_RUN_JSON);
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
