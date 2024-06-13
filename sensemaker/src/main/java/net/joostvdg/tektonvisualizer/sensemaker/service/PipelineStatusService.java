/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import java.util.List;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.sensemaker.db.InsertResult;

public interface PipelineStatusService {

  InsertResult newPipelineStatus(PipelineStatus pipelineStatus);

  boolean pipelineStatusExists(String pipelineName);

  PipelineStatus getPipelineStatusByName(String pipelineName);

  List<PipelineStatus> getAllPipelineStatuses();
}
