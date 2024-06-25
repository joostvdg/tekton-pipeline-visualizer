/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker;

import net.joostvdg.tektonvisualizer.sensemaker.messaging.RabbitReceiver;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
public class SensemakerApplication {

  private static final String QUEUE_NAME = "tekton";
  private static final String TOPIC_EXCHANGE_NAME = "tekton-exchange";
  private static final String ROUTING_KEY = "tekton.#";
  public static final String PIPELINE_STATUS_TOPIC = "pipeline-status";

  public static void main(String[] args) {
    SpringApplication.run(SensemakerApplication.class, args);
  }

  @Bean
  public Queue queue() {
    return new Queue(QUEUE_NAME, false);
  }

  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(TOPIC_EXCHANGE_NAME);
  }

  @Bean
  public Binding binding(Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
  }

  @Bean
  MessageListenerAdapter listenerAdapter(RabbitReceiver receiver) {
    return new MessageListenerAdapter(receiver, "receiveMessage");
  }

  @Bean
  SimpleMessageListenerContainer container(
      ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
    SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.setQueueNames(QUEUE_NAME);
    container.setMessageListener(listenerAdapter);
    return container;
  }

  @Bean
  public NewTopic topic() {
    return TopicBuilder.name(PIPELINE_STATUS_TOPIC)
            .partitions(10)
            .replicas(1)
            .build();
  }
}
