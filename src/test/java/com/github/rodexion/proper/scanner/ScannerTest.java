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

package com.github.rodexion.proper.scanner;


import static org.fest.assertions.api.Assertions.assertThat;

import org.fest.assertions.core.Condition;
import org.junit.Test;

/**
 * ScannerTest
 *
 * @author rodion
 * @version X.0
 * @since 2013/10/08
 */
public class ScannerTest {
  @Test
  public void scan() {
    ScanResult result = ProperScanners.scanner(ProperScanners.class.getPackage().getName()).scan();
    assertThat(result.getDeclarations()).hasSize(3)
            .haveExactly(1, declOf("test.prop.int", MyConstants.intPropLineNumber))
            .haveExactly(1, declOf("test.prop.lazyLong", MyConstants.lazyLongPropLineNumber))
            .haveExactly(1, declOf("test.float.prop", MyPropeties2.floatPropLineNumber));
  }

  private static Condition<ProperDecl> declOf(final String key, final int locatedAtLine) {
    return new Condition<ProperDecl>() {
      @Override
      public boolean matches(ProperDecl value) {
        return value.getLocation().getLineNumber() == locatedAtLine &&
                value.getProperty().getInfo().getKey().equals(key);
      }
    };
  }
}
