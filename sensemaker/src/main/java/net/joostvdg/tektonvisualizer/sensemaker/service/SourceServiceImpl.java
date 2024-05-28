/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.joostvdg.tektonvisualizer.model.Source;
import net.joostvdg.tektonvisualizer.model.tables.records.CodeSourceRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SourceServiceImpl implements SourceService {

  private final Logger logger = LoggerFactory.getLogger(SourceServiceImpl.class);

  private final DSLContext create;

  public SourceServiceImpl(DSLContext create) {
    this.create = create;
  }

  @Override
  public List<Source> getAllSources() {
    Result<CodeSourceRecord> result =
        create.selectFrom(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE).fetch();
    List<Source> sources = new ArrayList<>();
    for (CodeSourceRecord codeSourceRecord : result) {
      Source source = translateRecordToSource(codeSourceRecord);
      sources.add(source);
    }
    logger.info("Found {} Sources", sources.size());
    return sources;
  }

  private Source translateRecordToSource(CodeSourceRecord codeSourceRecord) {
    String identifier =
        codeSourceRecord.get(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.ID).toString();
    String name =
        codeSourceRecord.get(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.SOURCE_NAME);
    String type =
        codeSourceRecord.get(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.SOURCE_TYPE);
    String url =
        codeSourceRecord.get(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.SOURCE_URL);
    String subPath =
        codeSourceRecord.get(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.SUB_PATH);
    return new Source(identifier, name, type, url, subPath);
  }

  @Override
  public Optional<Source> getSourceById(String id) {
    Result<CodeSourceRecord> result =
        create
            .selectFrom(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE)
            .where(
                net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.ID.eq(Integer.valueOf(id)))
            .fetch();
    if (result.size() == 1) {
      return Optional.of(translateRecordToSource(result.getFirst()));
    }
    logger.warn("Found {} sources with id {}", result.size(), id);
    return Optional.empty();
  }

  @Override
  public List<Source> sourcesByUrl(String url) {
    Result<CodeSourceRecord> result =
        create
            .selectFrom(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE)
            .where(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.SOURCE_URL.eq(url))
            .fetch();
    List<Source> sources = new ArrayList<>();
    for (CodeSourceRecord codeSourceRecord : result) {
      Source source = translateRecordToSource(codeSourceRecord);
      sources.add(source);
    }
    return sources;
  }

  @Override
  public Optional<Source> sourceByUrlAndSubPath(String url, String subPath) {
    Result<CodeSourceRecord> result =
        create
            .selectFrom(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE)
            .where(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.SOURCE_URL.eq(url))
            .and(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.SUB_PATH.eq(subPath))
            .fetch();
    if (result.size() == 1) {
      return Optional.of(translateRecordToSource(result.getFirst()));
    }
    logger.warn("Found {} sources with url {} and subPath {}", result.size(), url, subPath);
    return Optional.empty();
  }

  @Override
  public Optional<Source> newSource(Source source) {
    var newSourceId =
        create
            .insertInto(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE)
            .set(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.SOURCE_NAME, source.name())
            .set(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.SOURCE_TYPE, source.type())
            .set(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.SOURCE_URL, source.url())
            .set(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.SUB_PATH, source.subPath())
            .returning(net.joostvdg.tektonvisualizer.model.Tables.CODE_SOURCE.ID)
            .fetchOne();
    logger.info("Inserted new Source with id {}", newSourceId);
    if (newSourceId != null) {
      return Optional.of(
          new Source(
              newSourceId.toString(),
              source.name(),
              source.type(),
              source.url(),
              source.subPath()));
    }
    return Optional.empty();
  }
}
