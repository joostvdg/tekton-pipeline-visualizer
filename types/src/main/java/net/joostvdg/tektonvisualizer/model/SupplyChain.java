/* (C)2024 */
package net.joostvdg.tektonvisualizer.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record SupplyChain(
    String identifier, String name, List<PipelineStatus> pipelineStatuses, List<Source> sources) {

  public SupplyChain {
    if (identifier.isBlank()) {
      identifier = UUID.randomUUID().toString();
    }
    Objects.requireNonNull(name, "Name cannot be null");
    Objects.requireNonNull(pipelineStatuses, "PipelineStatuses cannot be null");
    Objects.requireNonNull(sources, "Sources cannot be null");
  }
}
