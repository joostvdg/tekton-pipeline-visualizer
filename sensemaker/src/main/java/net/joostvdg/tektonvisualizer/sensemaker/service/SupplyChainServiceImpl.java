/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import java.util.ArrayList;
import java.util.List;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.model.Source;
import net.joostvdg.tektonvisualizer.model.SupplyChain;
import net.joostvdg.tektonvisualizer.model.Tables;
import net.joostvdg.tektonvisualizer.model.tables.records.PipelineStatusSupplyChainRecord;
import net.joostvdg.tektonvisualizer.model.tables.records.SupplyChainRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static net.joostvdg.tektonvisualizer.model.Tables.PIPELINE_STATUS;

@Service
@Transactional
public class SupplyChainServiceImpl implements SupplyChainService {

  private final Logger logger = LoggerFactory.getLogger(SupplyChainServiceImpl.class);

  private final DSLContext create;

  private final SourceService sourceService;

  public SupplyChainServiceImpl(DSLContext create, SourceService sourceService) {
    this.create = create;
    this.sourceService = sourceService;
  }

  @Override
  public Integer newSupplyChain(SupplyChain supplyChain) {
    return null;
  }

  @Override
  public SupplyChain getSupplyChainById(Integer id) {
    return null;
  }

  @Override
  public List<SupplyChain> getAllSupplyChains() {
    Result<SupplyChainRecord> result = create.selectFrom(Tables.SUPPLY_CHAIN).fetch();
    List<SupplyChain> supplyChains = new ArrayList<>();
    for (Record supplyChainRecord : result) {
      SupplyChain supplyChain = translateRecordToSupplyChain(supplyChainRecord);
      supplyChains.add(supplyChain);
    }
    logger.info("Found {} SupplyChains", supplyChains.size());

    return supplyChains;
  }

  private SupplyChain translateRecordToSupplyChain(Record supplyChainRecord) {
    String identifier = supplyChainRecord.get(Tables.SUPPLY_CHAIN.ID).toString();
    String name = supplyChainRecord.get(Tables.SUPPLY_CHAIN.NAME);
    List<PipelineStatus> pipelineStatuses = new ArrayList<>(); // TODO: fetch pipeline statuses
    List<Source> sources = new ArrayList<>(); // TODO: fetch sources

    return new SupplyChain(identifier, name, pipelineStatuses, sources);
  }

  @Override
  public boolean attachPipelineStatusToSupplyChain(Integer pipelineStatusId, String sourceUrl) {
    // retrieve the pipeline results to find the source URL and subPath
    if (pipelineStatusId == null) {
      throw new IllegalArgumentException("pipelineStatusId cannot be null");
    }

    // retrieve the supply chain that has the source URL and subPath
    var sources =
        sourceService.sourcesByUrl(sourceUrl); // TODO: update this when we do have the subPath
    if (sources.isEmpty()) {
      logger.warn("No sources found for URL {}", sourceUrl);
      return false;
    }

    // find supply chain that is linked to the source
    List<SupplyChain> supplyChains = retrieveSupplyChainsForSources(sources);

    // attach the pipeline status to the supply chain
    return attachSupplyChainToPipelineStatus(supplyChains, pipelineStatusId);
  }

  private boolean attachSupplyChainToPipelineStatus(
      List<SupplyChain> supplyChains, Integer pipelineStatusId) {
    if (supplyChains.isEmpty()) {
      logger.warn("No supply chains found for pipeline status {}", pipelineStatusId);
      return false;
    }

    if (supplyChains.size() > 1) {
      logger.warn("Multiple supply chains found for pipeline status {}", pipelineStatusId);
    }

    // Verify these two aren't matched already
    PipelineStatusSupplyChainRecord pipelineStatusSupplyChainRecord = create
        .selectFrom(Tables.PIPELINE_STATUS_SUPPLY_CHAIN)
        .where(Tables.PIPELINE_STATUS_SUPPLY_CHAIN.PIPELINE_STATUS_ID.eq(pipelineStatusId))
        .and(Tables.PIPELINE_STATUS_SUPPLY_CHAIN.SUPPLY_CHAIN_ID.eq(Integer.valueOf(supplyChains.getFirst().identifier())))
        .fetchOne();
    if (pipelineStatusSupplyChainRecord != null) {
        logger.warn("PipelineStatus {} is already attached to SupplyChain {}", pipelineStatusId, supplyChains.getFirst().identifier());
        return false;
    }

    // TODO: should we assume there's only one? or can we do more than one?
    create
        .insertInto(Tables.PIPELINE_STATUS_SUPPLY_CHAIN)
        .set(Tables.PIPELINE_STATUS_SUPPLY_CHAIN.PIPELINE_STATUS_ID, pipelineStatusId)
        .set(
            Tables.PIPELINE_STATUS_SUPPLY_CHAIN.SUPPLY_CHAIN_ID,
            Integer.valueOf(supplyChains.getFirst().identifier()))
        .execute();
    return true;
  }

  private List<SupplyChain> retrieveSupplyChainsForSources(List<Source> sources) {
    Result<Record> result =
        create
            .select()
            .from(Tables.SUPPLY_CHAIN)
            .join(Tables.SUPPLY_CHAIN_CODE_SOURCE)
            .on(Tables.SUPPLY_CHAIN.ID.eq(Tables.SUPPLY_CHAIN_CODE_SOURCE.SUPPLY_CHAIN_ID))
            .where(
                Tables.SUPPLY_CHAIN_CODE_SOURCE.CODE_SOURCE_ID.in(
                    sources.stream().map(Source::identifier).toList()))
            .fetch();

    List<SupplyChain> supplyChains = new ArrayList<>();
    for (Record record : result) {
      SupplyChain supplyChain = translateRecordToSupplyChain(record);
      supplyChains.add(supplyChain);
    }
    return supplyChains;
  }
}
