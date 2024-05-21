/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import java.util.List;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.model.SupplyChain;

public interface SupplyChainService {
  Integer newSupplyChain(SupplyChain supplyChain);

  SupplyChain getSupplyChainById(Integer id);

  List<SupplyChain> getAllSupplyChains();

  boolean attachPipelineStatusToSupplyChain(PipelineStatus pipelineStatus);
}
