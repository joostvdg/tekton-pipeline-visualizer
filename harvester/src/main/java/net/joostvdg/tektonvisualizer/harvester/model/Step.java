package net.joostvdg.tektonvisualizer.harvester.model;

import java.util.List;

public record Step(String name, String image, List<String> command, List<String> args) {}
