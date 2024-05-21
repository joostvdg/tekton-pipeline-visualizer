/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
public class StartDataConnections {
  static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

  static {
    postgres.start();
  }
}
