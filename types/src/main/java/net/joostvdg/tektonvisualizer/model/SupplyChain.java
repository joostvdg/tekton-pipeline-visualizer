/* (C)2024 */
package net.joostvdg.tektonvisualizer.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record SupplyChain(
    String identifier,
    String name,
    List<PipelineStatus> pipelineStatuses,
    List<Source> sources) { // Step 2: Add the List<Source> field

  public SupplyChain(String name, List<PipelineStatus> pipelineStatuses, List<Source> sources) {
    this(UUID.randomUUID().toString(), name, pipelineStatuses, sources);
  }

  public SupplyChain {
    Objects.requireNonNull(name, "Name cannot be null");
    Objects.requireNonNull(pipelineStatuses, "PipelineStatuses cannot be null");
    Objects.requireNonNull(sources, "Sources cannot be null"); // Step 4: Ensure sources is not null
  }
}
