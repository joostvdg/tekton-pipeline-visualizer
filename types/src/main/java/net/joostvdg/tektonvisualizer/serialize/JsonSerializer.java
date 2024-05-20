/* (C)2024 */
package net.joostvdg.tektonvisualizer.serialize;

import com.alibaba.fastjson2.JSON;
import net.joostvdg.tektonvisualizer.model.TektonResourceType;

public class JsonSerializer {
  public static String toJson(TektonResourceType tektonResourceType) {
    return JSON.toJSONString(tektonResourceType);
  }

  public static <T extends TektonResourceType> T fromJson(String json, Class<T> clazz) {
    return JSON.parseObject(json, clazz);
  }

  public static byte[] toJsonBytes(TektonResourceType tektonResourceType) {
    return JSON.toJSONBytes(tektonResourceType);
  }
}
