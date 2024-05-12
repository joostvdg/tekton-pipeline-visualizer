/* (C)2024 */
package net.joostvdg.tektonvisualizer.harvester.service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesApi;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import io.kubernetes.client.util.generic.options.ListOptions;
import net.joostvdg.tektonvisualizer.harvester.parser.TektonResourceParser;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.model.TektonResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class PipelineStatusImpl implements PipelineStatusService {

  private final ApiClient apiClient;

  private final TektonResourceParser pipelineStatusParser;

  private final Logger logger = LoggerFactory.getLogger(PipelineStatusImpl.class);

  private HashMap<String, PipelineStatus> knownPipelineStatuses;

  public PipelineStatusImpl(ApiClient apiClient, TektonResourceParser pipelineStatusParser) {
    this.apiClient = apiClient;
    this.pipelineStatusParser = pipelineStatusParser;
  }

  @PostConstruct
  private void initialize() {
    if (logger.isInfoEnabled()) {
      logger.info("Initializing %s bean.".formatted(this.getClass().getName()));
    }
    knownPipelineStatuses = new HashMap<>();
  }

  @Override
  public ResponseEntity<List<PipelineStatus>> getPipelineStatuses() {
    inspectCluster();
    var results = knownPipelineStatuses.values().stream().toList();
    return ResponseEntity.ok(results);
  }

  private void inspectCluster() {
    // TODO: optimize by retrieving a list of name and only retrieve the ones that are not yet known
    // TODO: store the ones that are known in a cache
    DynamicKubernetesApi dynamicApi =
        new DynamicKubernetesApi("tekton.dev", "v1", "pipelineruns", apiClient);

    ListOptions listOptions = new ListOptions();
    listOptions.setLimit(1000);

    var pipelineRuns = dynamicApi.list("idec", listOptions).getObject();
    if (pipelineRuns == null) {
      logger.warn("No pipelineRuns found (NULL.)");
      return;
    } else if (pipelineRuns.getItems().isEmpty()) {
      logger.info("No pipelineRuns found (EMPTY.)");
    } else {
      logger.info("Found {} pipelineRuns.", pipelineRuns.getItems().size());
    }

    for (var pipelineRun : pipelineRuns.getItems()) {
      var runName = pipelineRun.getMetadata().getName();
      if (knownPipelineStatuses.containsKey(runName)) {
          logger.debug("PipelineRun: {} already known.", runName);
          continue;
      }
      Optional<TektonResourceType> pipelineStatus = pipelineStatusParser.parse(pipelineRun);
      if (pipelineStatus.isEmpty()) {
        logger.warn("Could not parse PipelineRun: {}", runName);
        continue;
      }
      knownPipelineStatuses.put(runName, (PipelineStatus) pipelineStatus.get());
    }
  }
}
