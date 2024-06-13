/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.joostvdg.tektonvisualizer.model.Source;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class SourceServiceImplTest {

  @Autowired private SourceServiceImpl sourceServiceImpl;

  @Test
  void shouldFindAllSources() {
    List<Source> sources = sourceServiceImpl.getAllSources();
    assertFalse(sources.isEmpty());
    assertEquals(3, sources.size()); // TODO: find a better way
    sources.sort(Comparator.comparing(Source::name).reversed());
    Source source1 = sources.get(0);
    assertEquals("Tekton Pipeline Visualizer - Sensemaker", source1.name());
    assertEquals("Github", source1.type());
    assertEquals("https://github.com/joostvdg/tekton-pipeline-visualizer.git", source1.url());
    assertEquals("sensemaker", source1.subPath());
    Source source2 = sources.get(1);
    assertEquals("Tekton Pipeline Visualizer - Harvester", source2.name());
    assertEquals("Github", source2.type());
    assertEquals("https://github.com/joostvdg/tekton-pipeline-visualizer.git", source2.url());
    assertEquals("harvester", source2.subPath());
  }

  @Test
  void shouldFindSourceById() {
    Optional<Source> sourceOpt = sourceServiceImpl.getSourceById("1");
    assertFalse(sourceOpt.isEmpty());
    Source source = sourceOpt.get();
    assertEquals("IDEC", source.name());
    assertEquals("Github", source.type());
  }

  @Test
  void shouldFindSourcesByUrl() {
    List<Source> sources =
        sourceServiceImpl.sourcesByUrl(
            "https://github.com/joostvdg/tekton-pipeline-visualizer.git");
    assertFalse(sources.isEmpty());
    assertEquals(2, sources.size());
  }

  @Test
  void shouldFindSourceByUrlAndSubPath() {
    Optional<Source> sourceOpt =
        sourceServiceImpl.sourceByUrlAndSubPath(
            "https://github.com/joostvdg/tekton-pipeline-visualizer.git", "sensemaker");
    assertFalse(sourceOpt.isEmpty());
    Source source = sourceOpt.get();
    assertEquals("Tekton Pipeline Visualizer - Sensemaker", source.name());
    assertEquals("Github", source.type());
  }

  @Test
  void shouldInsertNewSource() {
    Source source =
        new Source(
            "",
            "Tekton Pipeline Visualizer - Notifier",
            "Github",
            "https://github.com/joostvdg/tekton-pipeline-visualizer.git",
            "notifier");
    Optional<Source> newSourceOpt = sourceServiceImpl.newSource(source);
    assertFalse(newSourceOpt.isEmpty());
    Source newSource = newSourceOpt.get();
    assertEquals("Tekton Pipeline Visualizer - Notifier", newSource.name());
    assertEquals("Github", newSource.type());
  }
}
