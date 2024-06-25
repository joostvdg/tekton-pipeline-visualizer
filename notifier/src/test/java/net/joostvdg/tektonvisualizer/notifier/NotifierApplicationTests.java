/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class NotifierApplicationTests {

  @Test
  void contextLoads() {}
}
