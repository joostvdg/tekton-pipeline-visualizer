/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.mapping;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "when")
public record When(EventStatus current, EventStatus previous) {

  @ConstructorBinding
  public When(EventStatus current, EventStatus previous) {
    this.current = current;
    this.previous = previous;
  }
}
