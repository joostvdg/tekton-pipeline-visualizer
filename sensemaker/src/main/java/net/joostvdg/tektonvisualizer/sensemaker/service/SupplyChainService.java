/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import java.util.List;
import net.joostvdg.tektonvisualizer.model.PipelineStatus;
import net.joostvdg.tektonvisualizer.model.SupplyChain;

public interface SupplyChainService {

  static final String RESULT_FIELD_REPO_URL = "REPO_URL";
  static final String RESULT_FIELD_SUB_PATH = "SUB_PATH"; // TODO: collect subPath in Pipeline

  Integer newSupplyChain(SupplyChain supplyChain);

  SupplyChain getSupplyChainById(Integer id);

  List<SupplyChain> getAllSupplyChains();

  boolean attachPipelineStatusToSupplyChain(PipelineStatus pipelineStatus);
}
