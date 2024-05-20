/* (C)2024 */
package net.joostvdg.tektonvisualizer.model;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record PipelineStatus(
    String identifier,
    String pipelineIdentifier,
    String name,
    Map<String, String> results,
    List<PipelineStage> stages,
    Status status,
    Instant instantOfCompletion,
    Instant instantOfStart,
    Duration duration,
    PipelineTrigger trigger) // Step 2: Add the PipelineTrigger field
    implements TektonResourceType {

  public PipelineStatus {
    Objects.requireNonNull(pipelineIdentifier, "PipelineIdentifier cannot be null");
    Objects.requireNonNull(name, "Name cannot be null");
    Objects.requireNonNull(status, "Status cannot be null");
    Objects.requireNonNull(instantOfCompletion, "instantOfCompletion cannot be null");
    Objects.requireNonNull(instantOfStart, "instantOfStart cannot be null");
    Objects.requireNonNull(trigger, "Trigger cannot be null"); // Ensure the trigger is not null
    if (duration == null) {
      duration = Duration.between(instantOfStart, instantOfCompletion);
    }
    if (identifier == null || identifier.isBlank()) {
      identifier = java.util.UUID.randomUUID().toString();
    }
  }

  public static class Builder {
    private String identifier;
    private String pipelineIdentifier;
    private String name;
    private Map<String, String> results;
    private List<PipelineStage> stages;
    private Status status;
    private Instant instantOfCompletion;
    private Instant instantOfStart;
    private Duration duration;
    private PipelineTrigger trigger; // Step 3: Add the PipelineTrigger field to the Builder

    public Builder identifier(String identifier) {
      this.identifier = identifier;
      return this;
    }

    public Builder pipelineIdentifier(String pipelineIdentifier) {
      this.pipelineIdentifier = pipelineIdentifier;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder results(Map<String, String> results) {
      this.results = results;
      return this;
    }

    public Builder stages(List<PipelineStage> stages) {
      this.stages = stages;
      return this;
    }

    public Builder status(Status status) {
      this.status = status;
      return this;
    }

    public Builder instantOfCompletion(Instant instantOfCompletion) {
      this.instantOfCompletion = instantOfCompletion;
      return this;
    }

    public Builder instantOfStart(Instant instantOfStart) {
      this.instantOfStart = instantOfStart;
      return this;
    }

    public Builder duration(Duration duration) {
      this.duration = duration;
      return this;
    }

    public Builder trigger(
        PipelineTrigger trigger) { // Step 4: Add a method to set the PipelineTrigger
      this.trigger = trigger;
      return this;
    }

    public PipelineStatus build() {
      return new PipelineStatus(
          identifier,
          pipelineIdentifier,
          name,
          results,
          stages,
          status,
          instantOfCompletion,
          instantOfStart,
          duration,
          trigger); // Include the trigger in the constructor call
    }
  }

  @Override
  public String toString() {
    return "PipelineStatus{"
        + "identifier='"
        + identifier
        + '\''
        + ", pipelineIdentifier='"
        + pipelineIdentifier
        + '\''
        + ", name='"
        + name
        + '\''
        + ", results="
        + results
        + ", stages="
        + stages
        + ", status="
        + status
        + ", instantOfCompletion="
        + instantOfCompletion
        + ", instantOfStart="
        + instantOfStart
        + ", duration="
        + duration
        + ", trigger="
        + trigger
        + '}';
  }
}
