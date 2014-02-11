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
import com.github.rodexion.proper.scanner.ProperDecl;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Internal class used for accumulating detected
 * system property declarations.</p>
 *
 * @author rodexion
 * @since 0.1
 */
public class InternalBuilderBus {
  private static final List<ProperDecl> properties = new ArrayList<>();

  /**
   * <p>Register the given property as detected.</p>
   *
   * @param info     Property meta data (not-null)
   * @param location Property location data (not-null)
   */
  public static void firePropertyBuilt(Proper.Ty<?> info, ProperLocation location) {
    properties.add(new ProperDecl(info, location));
  }

  /**
   * <p>Retrieve all accumulated properties</p>
   *
   * @return All accumulated properties (not-null)
   */
  public static List<ProperDecl> getFoundProperties() {
    return new ArrayList<>(properties);
  }
}
