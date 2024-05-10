package net.joostvdg.tektonvisualizer.harvester.model;

import java.util.Map;

public record TriggerTemplate(String name, Pipeline pipelineRef, Map<String, String> params, Map<String, String> resources) {}
