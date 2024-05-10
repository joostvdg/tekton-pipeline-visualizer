package net.joostvdg.tektonvisualizer.harvester.model;

import java.util.List;

public record ClusterTask(String name, List<Step> steps) {}
