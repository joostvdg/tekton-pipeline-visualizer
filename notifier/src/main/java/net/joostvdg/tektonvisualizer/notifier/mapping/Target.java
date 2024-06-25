/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.mapping;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "target")
public record Target(RoutingType routingType, String url, When when) {

  public Target(RoutingType routingType, String url, When when) {
    this.routingType = routingType;
    this.url = url;
    this.when = when;
  }
}
