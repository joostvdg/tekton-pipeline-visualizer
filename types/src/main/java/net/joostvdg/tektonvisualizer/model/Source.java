/* (C)2024 */
package net.joostvdg.tektonvisualizer.model;

import java.util.Objects;

public record Source(String identifier, String name, String type, String url, String subPath) {

  public Source {
    Objects.requireNonNull(identifier, "Identifier cannot be null");
    Objects.requireNonNull(name, "Name cannot be null");
    Objects.requireNonNull(type, "Type cannot be null");
    Objects.requireNonNull(url, "URL cannot be null");
    // subPath can be null, indicating no specific sub-path within the source
  }
}
