/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.cloudevents;

public record CloudEventBase(
    String specversion,
    String type,
    String source,
    String id,
    String time,
    String datacontenttype,
    EventData data) {}
