/* (C)2024 */
package net.joostvdg.tektonvisualizer.harvester.messaging;

import net.joostvdg.tektonvisualizer.model.PipelineStatus;

public interface QueueSender {
  void sendPipelineStatus(PipelineStatus pipelineStatus);
}
