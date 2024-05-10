/* (C)2024 */
package net.joostvdg.tektonvisualizer.model;

public record Status(boolean success, String explanation, ExecutionStatus executionStatus) {}
