package net.joostvdg.tektonvisualizer.harvester.model;

import java.util.Map;

public record PipelineRun(String name, Pipeline pipeline, Map<String, PipelineResource> resources) {}
