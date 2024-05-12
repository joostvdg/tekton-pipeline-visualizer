/* (C)2024 */
package net.joostvdg.tektonvisualizer.harvester.parser;

import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;
import java.util.Optional;
import net.joostvdg.tektonvisualizer.model.TektonResourceType;

public interface TektonResourceParser {

  public Optional<TektonResourceType> parse(DynamicKubernetesObject resource);
}
