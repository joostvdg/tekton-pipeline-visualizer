/* (C)2024 */
package net.joostvdg.tektonvisualizer.sensemaker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import net.joostvdg.tektonvisualizer.model.SupplyChain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class SupplyChainStatusTest {

  @Autowired private SupplyChainServiceImpl supplyChainServiceImpl;

  @Test
  void shouldFindAllSupplyChainss() {
    List<SupplyChain> supplyChains = supplyChainServiceImpl.getAllSupplyChains();
    assertFalse(supplyChains.isEmpty());
    assertEquals(1, supplyChains.size());
  }
}
