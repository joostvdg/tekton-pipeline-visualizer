/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.pipeline;

import java.util.Optional;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.notifier.mapping.NotificationMapping;

public interface PipelineProcessor {

  void process(PipelineStatus pipelineStatus);

  Optional<NotificationMapping> getMapping(PipelineStatus pipelineStatus);
}
