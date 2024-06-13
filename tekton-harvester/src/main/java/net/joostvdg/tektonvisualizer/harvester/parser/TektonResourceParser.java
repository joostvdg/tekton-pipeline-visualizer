/* (C)2024 */
package net.joostvdg.tektonvisualizer.harvester.parser;

import io.kubernetes.client.util.generic.dynamic.DynamicKubernetesObject;
import java.util.List;
import java.util.Optional;
import net.joostvdg.tektonvisualizer.model.TektonResourceType;

public interface TektonResourceParser {

  /**
   * Parse a single Tekton Resource.
   *
   * @param resource the resource to parse
   * @return the parsed resource
   */
  Optional<TektonResourceType> parse(DynamicKubernetesObject resource);

  /**
   * Parse a list of Tekton Resources.
   *
   * @param resource the resource to parse
   * @return the parsed resources
   */
  List<TektonResourceType> parseList(DynamicKubernetesObject resource);
}
