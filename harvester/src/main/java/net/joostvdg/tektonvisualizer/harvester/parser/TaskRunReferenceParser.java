/* (C)2024 */
package net.joostvdg.tektonvisualizer.harvester.parser;

import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;
import java.util.Optional;
import net.joostvdg.tektonvisualizer.model.TektonResourceType;
import org.springframework.stereotype.Service;

/** Parses the TaskRun reference from a PipelineRun's Status. */
@Service
public class TaskRunReferenceParser implements TektonResourceParser {

  @Override
  public Optional<TektonResourceType> parse(DynamicKubernetesObject resource) {

    // TODO: implement the TaskRunReferenceParser
    // This parser should parse the TaskRun reference from a PipelineRun's Status
    // And it should return PipelineStages which contain PipelineSteps if applicable
    return Optional.empty();
  }
}
