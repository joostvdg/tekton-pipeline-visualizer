/* (C)2024 */
package net.joostvdg.tektonvisualizer.model;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public record PipelineStage(
    String identifier,
    String name,
    Status status,
    Duration duration,
    Integer order,
    List<PipelineStep> steps) {
  public PipelineStage {
    Objects.requireNonNull(identifier, "Identifier cannot be null");
    Objects.requireNonNull(name, "Name cannot be null");
    Objects.requireNonNull(status, "Status cannot be null");
    Objects.requireNonNull(duration, "Duration cannot be null");
    Objects.requireNonNull(order, "Order cannot be null");
    Objects.requireNonNull(steps, "Steps cannot be null");
  }
}
