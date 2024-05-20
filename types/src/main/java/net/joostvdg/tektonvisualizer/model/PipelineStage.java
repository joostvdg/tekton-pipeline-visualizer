/* (C)2024 */
package net.joostvdg.tektonvisualizer.model;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record PipelineStage(
    String identifier,
    String name,
    Status status,
    Duration duration,
    Integer order,
    List<PipelineStep> steps)
    implements TektonResourceType {
  public PipelineStage {
    if (identifier == null || identifier.isBlank()) {
      identifier = UUID.randomUUID().toString();
    }
    Objects.requireNonNull(name, "Name cannot be null");
    Objects.requireNonNull(status, "Status cannot be null");
    Objects.requireNonNull(duration, "Duration cannot be null");
    Objects.requireNonNull(order, "Order cannot be null");
    Objects.requireNonNull(steps, "Steps cannot be null");
  }

  public static class Builder {
    private String identifier;
    private String name;
    private Status status;
    private Duration duration;
    private Integer order;
    private List<PipelineStep> steps;

    public Builder identifier(String identifier) {
      this.identifier = identifier;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder status(Status status) {
      this.status = status;
      return this;
    }

    public Builder duration(Duration duration) {
      this.duration = duration;
      return this;
    }

    public Builder order(Integer order) {
      this.order = order;
      return this;
    }

    public Builder steps(List<PipelineStep> steps) {
      this.steps = steps;
      return this;
    }

    public PipelineStage build() {
      return new PipelineStage(identifier, name, status, duration, order, steps);
    }
  }
}
