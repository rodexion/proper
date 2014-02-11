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

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author rodexion
 * @since 0.1
 */
public class ComplexPropertyTest {
  @Rule
  public TestRule tmpProps = RuleUtils.tmpSysProp("my.key.test1", "my.key.test2");

  @Test
  public void propertiesCanAcceptSubstitutions() {
    System.setProperty("my.key.test1", "value1");
    System.setProperty("my.key.test2", "value2");

    Proper.Ty<String> prop = Proper.tyBuilder("my.key.{0}", "default").build();
    assertThat(prop.getValue()).isEqualTo("default");
    assertThat(prop.getValue("test1")).isEqualTo("value1");
    assertThat(prop.getValue("test2")).isEqualTo("value2");
    assertThat(prop.getValue("does-not-exist")).isEqualTo("default");
    assertThat(prop.getValue(123)).isEqualTo("default");
  }
}
