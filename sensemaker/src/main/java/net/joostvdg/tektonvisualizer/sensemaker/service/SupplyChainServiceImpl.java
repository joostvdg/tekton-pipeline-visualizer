/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import java.util.ArrayList;
import java.util.List;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.model.Source;
import net.joostvdg.tektonvisualizer.model.SupplyChain;
import net.joostvdg.tektonvisualizer.model.Tables;
import net.joostvdg.tektonvisualizer.model.tables.records.SupplyChainRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SupplyChainServiceImpl implements SupplyChainService {

  private final Logger logger = LoggerFactory.getLogger(SupplyChainServiceImpl.class);

  private final DSLContext create;

  public SupplyChainServiceImpl(DSLContext create) {
    this.create = create;
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

  private SupplyChain translateRecordToSupplyChain(Record record) {
    String identifier = record.get(Tables.SUPPLY_CHAIN.ID).toString();
    String name = record.get(Tables.SUPPLY_CHAIN.NAME);
    List<PipelineStatus> pipelineStatuses = new ArrayList<>(); // TODO: fetch pipeline statuses
    List<Source> sources = new ArrayList<>(); // TODO: fetch sources

    return new SupplyChain(identifier, name, pipelineStatuses, sources);
  }

  @Override
  public boolean attachPipelineStatusToSupplyChain(PipelineStatus pipelineStatus) {
    return false;
  }
}
