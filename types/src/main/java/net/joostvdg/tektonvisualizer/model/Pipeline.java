/* (C)2024 */
package net.joostvdg.tektonvisualizer.model;

import java.util.Objects;

public record Pipeline(String name, String namespace, String cluster, String identifier)
    implements TektonResourceType {
  public Pipeline {
    Objects.requireNonNull(name, "Name cannot be null");
    Objects.requireNonNull(namespace, "Namespace cannot be null");
    Objects.requireNonNull(cluster, "Cluster cannot be null");
    if (identifier == null || identifier.isBlank()) {
      identifier = generateIdentifier(name, namespace, cluster);
    }
  }

  private static String generateIdentifier(String name, String namespace, String cluster) {
    return name + "-" + namespace + "-" + cluster;
  }

  public String name() {
    return name;
  }

  public String namespace() {
    return namespace;
  }

  public String cluster() {
    return cluster;
  }
}
