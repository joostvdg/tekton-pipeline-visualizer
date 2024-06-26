/* (C)2024 */
package net.joostvdg.tektonvisualizer.harvester.service;

import java.util.List;

import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import org.springframework.http.ResponseEntity;

public interface PipelineStatusService {

  ResponseEntity<List<PipelineStatus>> getPipelineStatuses();

  void processPipelineRun(DynamicKubernetesObject pipelineRun);
}
