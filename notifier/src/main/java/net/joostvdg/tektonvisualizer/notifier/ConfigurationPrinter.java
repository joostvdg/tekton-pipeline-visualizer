/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier;

import jakarta.annotation.PostConstruct;
import net.joostvdg.tektonvisualizer.notifier.mapping.NotificationMappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationPrinter {

  private final NotificationMappings notificationMappings;

  @Autowired
  public ConfigurationPrinter(NotificationMappings notificationMappings) {
    this.notificationMappings = notificationMappings;
  }

  @PostConstruct
  public void printConfiguration() {
    System.out.println("Notification Mappings:");
    notificationMappings.mappings().forEach(System.out::println);
  }
}
