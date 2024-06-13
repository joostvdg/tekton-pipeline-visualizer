/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.db;

import java.util.Optional;

public record InsertResult(Boolean success, Optional<Integer> newRecordId) {}
