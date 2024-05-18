/* (C)2024 */
package net.joostvdg.tektonvisualizer.model;

import java.util.Objects;

public record PipelineTrigger(
    TriggerType triggerType,
    String trigger,
    String eventId,
    boolean rerun,
    String rerunOf,
    String eventListener) {

  public PipelineTrigger {
    Objects.requireNonNull(triggerType, "TriggerType cannot be null");
    Objects.requireNonNull(trigger, "Trigger cannot be null");
    Objects.requireNonNull(eventId, "EventId cannot be null");
    // rerun is a primitive boolean, so it doesn't require null check
    // rerunOf and eventListener can be null, indicating no rerun or unspecified event listener
  }
}
