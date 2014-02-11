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

package com.github.rodexion.proper.bus;

import com.github.rodexion.proper.Proper;
import lombok.Data;

/**
 * <p>Represents location in the source code where the system
 * property was declared.</p>
 *
 * @author rodexion
 * @since 0.1
 */
@Data
public class ProperLocation {
  /**
   * <p>Used when property location could not be determined, or is unapplicable.</p>
   */
  public static final ProperLocation UNKNOWN = new ProperLocation("UNKNOWN", -1);
  /**
   * <p>Source file where property is declared.</p>
   */
  private final String fileName;
  /**
   * <p>Line number in the source file where property is declared.</p>
   */
  private final int lineNumber;

  public static ProperLocation getCurrentDeclarationLocation() {
    for (StackTraceElement elem : new Exception().getStackTrace()) {
      if (!frameWorkElem(elem)) {
        return new ProperLocation(elem.getFileName(), elem.getLineNumber());
      }
    }
    return UNKNOWN;
  }

  private static boolean frameWorkElem(StackTraceElement elem) {
    String properRootPkgName = Proper.class.getPackage().getName();
    return elem.getClassName().startsWith(properRootPkgName) &&
            !elem.getMethodName().equals("<clinit>");
  }
}
