package net.joostvdg.tektonvisualizer.harvester.model;

import java.util.List;

public record Pipeline(String name, List<Task> tasks) {}
