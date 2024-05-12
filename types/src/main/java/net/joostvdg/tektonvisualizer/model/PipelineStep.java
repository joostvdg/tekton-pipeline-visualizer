/* (C)2024 */
package net.joostvdg.tektonvisualizer.model;

import java.util.Objects;

public record PipelineStep(String identifier, String name, Status status)
    implements TektonResourceType {
  public PipelineStep {
    Objects.requireNonNull(identifier, "Identifier cannot be null");
    Objects.requireNonNull(name, "Name cannot be null");
    Objects.requireNonNull(status, "Status cannot be null");
  }
}
