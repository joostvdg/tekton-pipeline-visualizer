/* (C)2024 */
package net.joostvdg.tektonvisualizer.harvester.messaging;

import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.serialize.JsonSerializer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitSender implements QueueSender {
  private final RabbitTemplate rabbitTemplate;

  public RabbitSender(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void sendPipelineStatus(PipelineStatus pipelineStatus) {
    rabbitTemplate.convertAndSend("tekton", JsonSerializer.toJson(pipelineStatus));
  }
}
