/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestSensemakerApplication {

  @Bean
  @ServiceConnection
  RabbitMQContainer rabbitContainer() {
    return new RabbitMQContainer(DockerImageName.parse("rabbitmq:latest"));
  }

  public static void main(String[] args) {
    SpringApplication.from(SensemakerApplication::main)
        .with(TestSensemakerApplication.class)
        .run(args);
  }
}
