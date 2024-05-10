package net.joostvdg.tektonvisualizer.harvester.model;

import java.time.Duration;
import java.time.LocalDateTime;

public record Status(boolean success, Duration duration, LocalDateTime startTime, LocalDateTime endTime, String StatusMessage) {}
