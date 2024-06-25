/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.routing;

import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.notifier.mapping.NotificationMapping;

public interface WebhookRouter {

  boolean route(NotificationMapping notificationMapping, PipelineStatus pipelineStatus);
}
