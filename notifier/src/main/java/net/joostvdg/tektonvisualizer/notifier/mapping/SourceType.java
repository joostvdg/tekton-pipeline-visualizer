/* (C)2024 */
package net.joostvdg.tektonvisualizer.notifier.mapping;

public enum SourceType {
  SOURCE_BUILD("source-build"),
  IMAGE_BUILD("image-build"),
  IMAGE_SCAN("image-scan"),
  ;

  private final String sourceType;

  SourceType(String sourceType) {
    this.sourceType = sourceType;
  }

  public String getSourceType() {
    return sourceType;
  }

  public static SourceType fromString(String sourceType) {
    for (SourceType type : SourceType.values()) {
      if (type.sourceType.equalsIgnoreCase(sourceType)) {
        return type;
      }
    }
    return null;
  }
}
