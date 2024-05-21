/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import static net.joostvdg.tektonvisualizer.model.Tables.*;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.joostvdg.tektonvisualizer.model.*;
import net.joostvdg.tektonvisualizer.model.tables.records.PipelineStatusRecord;
import net.joostvdg.tektonvisualizer.model.tables.records.PipelineTriggerRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PipelineStatusServiceImpl implements PipelineStatusService {

  private final Logger logger = LoggerFactory.getLogger(PipelineStatusServiceImpl.class);

  private final DSLContext create;

  public PipelineStatusServiceImpl(DSLContext create) {
    this.create = create;
  }

  @Override
  public Integer newPipelineStatus(PipelineStatus pipelineStatus) {
    logger.info("Received new PipelineStatus: {}", pipelineStatus);
    if (pipelineStatus == null) {
      throw new IllegalArgumentException("PipelineStatus cannot be null");
    }
    if (pipelineStatus.name() == null || pipelineStatus.name().isEmpty()) {
      throw new IllegalArgumentException("PipelineName cannot be null or empty");
    }
    PipelineStatusRecord pipelineStatusRecord =
        create
            .insertInto(PIPELINE_STATUS)
            .set(PIPELINE_STATUS.PIPELINE_ID, pipelineStatus.pipelineIdentifier())
            .set(PIPELINE_STATUS.NAME, pipelineStatus.name())
            .set(PIPELINE_STATUS.SUCCESS, pipelineStatus.status().success())
            .set(PIPELINE_STATUS.COMPLETION_MESSAGE, pipelineStatus.status().explanation())
            .set(
                PIPELINE_STATUS.START_TIMESTAMP,
                convertToTimestamp(pipelineStatus.instantOfStart()))
            .set(
                PIPELINE_STATUS.DURATION,
                convertToDuration(
                    pipelineStatus.instantOfStart(), pipelineStatus.instantOfCompletion()))
            .returning(PIPELINE_STATUS.ID)
            .fetchOne();

    if (pipelineStatusRecord == null) {
      logger.error("Failed to insert PipelineStatus: {}", pipelineStatus);
      return 0;
    }

    // create a new PipelineTrigger record
    PipelineTriggerRecord pipelineTriggerRecord =
        create
            .insertInto(PIPELINE_TRIGGER)
            .set(PIPELINE_TRIGGER.PIPELINE_STATUS_ID, pipelineStatusRecord.get(PIPELINE_STATUS.ID))
            .set(PIPELINE_TRIGGER.TRIGGER_TYPE, pipelineStatus.trigger().triggerType().name())
            .set(PIPELINE_TRIGGER.EVENT_ID, pipelineStatus.trigger().eventId())
            .set(PIPELINE_TRIGGER.RERUN_OF, pipelineStatus.trigger().rerunOf())
            .set(PIPELINE_TRIGGER.EVENT_LISTENER, pipelineStatus.trigger().eventListener())
            .returning(PIPELINE_TRIGGER.ID)
            .fetchOne();
    if (pipelineTriggerRecord == null) {
      logger.error("Failed to insert PipelineTrigger: {}", pipelineStatus.trigger());
      return 0;
    }

    return pipelineStatusRecord.get(PIPELINE_STATUS.ID);
  }

  private Long convertToDuration(Instant start, Instant end) {
    return end.toEpochMilli() - start.toEpochMilli();
  }

  // TODO: collect proper Zone/Offset
  private OffsetDateTime convertToTimestamp(Instant instant) {
    return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
  }

  @Override
  public boolean pipelineStatusExists(String pipelineName) {
    return false;
  }

  @Override
  public PipelineStatus getPipelineStatusByName(String pipelineName) {
    return null;
  }

  @Override
  public List<PipelineStatus> getAllPipelineStatuses() {
    List<PipelineStatus> pipelineStatuses = new ArrayList<>();
    Result<org.jooq.Record> result = create.select().from(PIPELINE_STATUS).fetch();
    for (org.jooq.Record record : result) {
      // boolean success, String explanation, ExecutionStatus executionStatus
      var success = record.getValue(PIPELINE_STATUS.SUCCESS, Boolean.class);
      var explanation = record.getValue(PIPELINE_STATUS.COMPLETION_MESSAGE, String.class);
      var executionStatus = ExecutionStatus.SUCCEEDED;
      if (!success) {
        executionStatus = ExecutionStatus.FAILED;
      }
      var status = new Status(success, explanation, executionStatus);
      Instant start =
          record.getValue(PIPELINE_STATUS.START_TIMESTAMP, OffsetDateTime.class).toInstant();
      Duration duration = Duration.ofMillis(record.getValue(PIPELINE_STATUS.DURATION, Long.class));
      Instant end = start.plus(duration);

      Integer pipelineStatusId = record.getValue(PIPELINE_STATUS.ID, Integer.class);
      // TODO: add stages and triggers to the pipelineStatus once we record them
      var stages = getStagesForPipelineStatus(pipelineStatusId);
      PipelineTrigger trigger = getTriggerForPipelineStatus(pipelineStatusId);
      Map<String, String> results = getResultsForPipelineStatus(pipelineStatusId);

      PipelineStatus pipelineStatus =
          new PipelineStatus.Builder()
              .pipelineIdentifier(record.getValue(PIPELINE_STATUS.PIPELINE_ID, String.class))
              .name(record.getValue(PIPELINE_STATUS.NAME, String.class))
              .status(status)
              .instantOfStart(
                  record
                      .getValue(PIPELINE_STATUS.START_TIMESTAMP, OffsetDateTime.class)
                      .toInstant())
              .instantOfCompletion(end)
              .stages(stages)
              .trigger(trigger)
              .results(results)
              .build();
      pipelineStatuses.add(pipelineStatus);
    }
    return pipelineStatuses;
  }

  private PipelineTrigger getTriggerForPipelineStatus(Integer pipelineStatusId) {
    // query the database for the trigger that belongs to the pipelineStatusId and then return it
    org.jooq.Record record =
        create
            .select()
            .from(PIPELINE_TRIGGER)
            .where(PIPELINE_TRIGGER.PIPELINE_STATUS_ID.eq(pipelineStatusId))
            .fetchOne();
    if (record == null) {
      return null;
    }
    TriggerType triggerType =
        TriggerType.valueOf(record.getValue(PIPELINE_TRIGGER.TRIGGER_TYPE, String.class));
    String trigger = "N/A"; // TODO: what should this be, I forgot...
    String eventId = record.getValue(PIPELINE_TRIGGER.EVENT_ID, String.class);
    String rerunOf = record.getValue(PIPELINE_TRIGGER.RERUN_OF, String.class);
    boolean rerun = rerunOf != null && !rerunOf.isEmpty();
    String eventListener = record.getValue(PIPELINE_TRIGGER.EVENT_LISTENER, String.class);
    return new PipelineTrigger(triggerType, trigger, eventId, rerun, rerunOf, eventListener);
  }

  private Map<String, String> getResultsForPipelineStatus(Integer pipelineStatusId) {
    // query the database for the results that belong to the pipelineStatusId and then return them
    // as a Map
    Result<org.jooq.Record> result =
        create
            .select()
            .from(PIPELINE_RESULT)
            .where(PIPELINE_RESULT.PIPELINE_STATUS_ID.eq(pipelineStatusId))
            .fetch();
    Map<String, String> results = new HashMap<>();
    for (org.jooq.Record record : result) {
      results.put(
          record.getValue(PIPELINE_RESULT.KEY, String.class),
          record.getValue(PIPELINE_RESULT.VALUE, String.class));
    }
    return results;
  }

  private List<net.joostvdg.tektonvisualizer.model.PipelineStage> getStagesForPipelineStatus(
      Integer pipelineStatusId) {
    Result<org.jooq.Record> result =
        create
            .select()
            .from(PIPELINE_STAGE)
            .where(PIPELINE_STAGE.PIPELINE_STATUS_ID.eq(pipelineStatusId))
            .fetch();
    List<net.joostvdg.tektonvisualizer.model.PipelineStage> stages = new ArrayList<>();
    for (org.jooq.Record record : result) {

      var success = record.getValue(PIPELINE_STATUS.SUCCESS, Boolean.class);
      var explanation = record.getValue(PIPELINE_STATUS.COMPLETION_MESSAGE, String.class);
      var executionStatus = ExecutionStatus.SUCCEEDED;
      if (!success) {
        executionStatus = ExecutionStatus.FAILED;
      }
      var status = new Status(success, explanation, executionStatus);

      var stage =
          new net.joostvdg.tektonvisualizer.model.PipelineStage.Builder()
              .identifier(record.getValue(PIPELINE_STAGE.ID, String.class))
              .name(record.getValue(PIPELINE_STAGE.NAME, String.class))
              .status(status)
              .duration(Duration.ofMillis(record.getValue(PIPELINE_STAGE.DURATION, Long.class)))
              .order(record.getValue(PIPELINE_STAGE.ORDER_NUMBER, Integer.class))
              .steps(new ArrayList<>())
              .build();
      stages.add(stage);
    }
    return stages;
  }
}
