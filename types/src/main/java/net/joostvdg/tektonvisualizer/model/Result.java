/* (C)2024 */
package net.joostvdg.tektonvisualizer.model;

import java.net.URI;
import java.util.Objects;

public record Result(String identifier, String name, String plainValue, URI uri)
    implements TektonResourceType {
  public Result {
    Objects.requireNonNull(identifier, "Identifier cannot be null");
    Objects.requireNonNull(name, "Name cannot be null");
    Objects.requireNonNull(plainValue, "PlainValue cannot be null");
    Objects.requireNonNull(uri, "URI cannot be null");
  }
}
