/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import java.util.List;
import java.util.Optional;
import net.joostvdg.tektonvisualizer.model.Source;

public interface SourceService {

  List<Source> getAllSources();

  Optional<Source> getSourceById(String id);

  // TODO: can we make URL unique?
  List<Source> sourcesByUrl(String url);

  // TODO: or can we only make URL + subPath unique?
  Optional<Source> sourceByUrlAndSubPath(String url, String subPath);

  Optional<Source> newSource(Source source);
}
