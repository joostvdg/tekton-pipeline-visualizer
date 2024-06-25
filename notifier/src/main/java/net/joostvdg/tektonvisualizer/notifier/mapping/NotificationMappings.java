/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.mapping;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notifications")
public record NotificationMappings(List<NotificationMapping> mappings) {

  public NotificationMappings(List<NotificationMapping> mappings) {
    this.mappings = mappings;
  }
}
