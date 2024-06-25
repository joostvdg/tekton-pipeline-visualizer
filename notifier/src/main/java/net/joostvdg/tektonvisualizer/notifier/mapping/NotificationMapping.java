/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.mapping;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mappings")
public record NotificationMapping(String name, boolean enabled, Source source, Target target) {

  public NotificationMapping(String name, boolean enabled, Source source, Target target) {
    this.name = name;
    this.enabled = enabled;
    this.source = source;
    this.target = target;
  }
}
