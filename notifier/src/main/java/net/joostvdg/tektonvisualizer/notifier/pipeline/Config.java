/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.pipeline;

import static net.joostvdg.tektonvisualizer.notifier.AppConfig.PIPELINE_STATUS_TOPIC;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class Config {
  @Bean
  public NewTopic pipelineStatusTopic() {
    return TopicBuilder.name(PIPELINE_STATUS_TOPIC).partitions(10).replicas(1).build();
  }
}
