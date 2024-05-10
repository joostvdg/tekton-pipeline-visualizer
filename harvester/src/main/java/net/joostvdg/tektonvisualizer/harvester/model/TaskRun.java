package net.joostvdg.tektonvisualizer.harvester.model;

import java.util.Map;

public record TaskRun(String name, Task task, Map<String, String> inputs) {}
