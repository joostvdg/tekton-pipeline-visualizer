/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier;

import org.springframework.boot.SpringApplication;

public class TestNotifierApplication {

  public static void main(String[] args) {
    SpringApplication.from(NotifierApplication::main)
        .with(TestcontainersConfiguration.class)
        .run(args);
  }
}
