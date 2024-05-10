package net.joostvdg.tektonvisualizer.harvester.model;

import java.util.List;
import java.util.Map;

public record Task(String name, List<Step> steps) {}


