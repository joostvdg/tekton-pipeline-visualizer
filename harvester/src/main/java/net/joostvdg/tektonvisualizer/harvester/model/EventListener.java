package net.joostvdg.tektonvisualizer.harvester.model;

import java.util.List;

public record EventListener(String name, String eventType, List<TriggerTemplate> triggerTemplates) {}
