/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import net.joostvdg.tektonvisualizer.model.PipelineStatus;

import java.util.List;

public interface PipelineStatusService {

  boolean newPipelineStatus(PipelineStatus pipelineStatus);

  boolean pipelineStatusExists(String pipelineName);

  PipelineStatus getPipelineStatusByName(String pipelineName);

  List<PipelineStatus> getAllPipelineStatuses();
}
