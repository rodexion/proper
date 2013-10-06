package com.github.rodexion.proper;

import org.junit.rules.ExternalResource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rodexion
 * @since 0.1
 */
public class RuleUtils {
  public static TmpSysProp tmpSysProp(String... keys) {
    return new TmpSysProp(keys);
  }

  public static final class TmpSysProp extends ExternalResource {
    private final Map<String, String> props = new HashMap<>();
    private final Map<String, String> old = new HashMap<>();

    public TmpSysProp(String... keys) {
      for (String key : keys) {
        props.put(key, null);
      }
    }

    public TmpSysProp set(String key, String value) {
      props.put(key, value);
      return this;
    }

    @Override
    protected void before() throws Throwable {
      for (Map.Entry<String, String> entry : props.entrySet()) {
        old.put(entry.getKey(), System.getProperty(entry.getKey()));
        setSysProp(entry);
      }
    }

    @Override
    protected void after() {
      for (Map.Entry<String, String> entry : old.entrySet()) {
        setSysProp(entry);
      }
      old.clear();
    }

    private void setSysProp(Map.Entry<String, String> entry) {
      if (null == entry.getValue()) {
        System.clearProperty(entry.getKey());
      } else {
        System.setProperty(entry.getKey(), entry.getValue());
      }
    }
  }
}
