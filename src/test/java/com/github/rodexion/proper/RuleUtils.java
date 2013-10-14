/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

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
