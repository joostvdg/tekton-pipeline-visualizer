/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.mapping;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@ConfigurationProperties(prefix = "notifications.mappings.source")
public record Source(String gitUrl, String subPath, SourceType type) {

  @ConstructorBinding
  public Source(String gitUrl, String subPath, SourceType type) {
    this.type = type;
    this.gitUrl = gitUrl;
    this.subPath = subPath;
  }
}
