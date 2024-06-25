/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier;

import net.joostvdg.tektonvisualizer.notifier.mapping.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
  NotificationMappings.class,
  NotificationMapping.class,
  Source.class,
  Target.class,
  When.class
})
public class AppConfig {
  public static final String PIPELINE_STATUS_TOPIC = "pipeline-status";

  private AppConfig() {
    super();
  }
}
