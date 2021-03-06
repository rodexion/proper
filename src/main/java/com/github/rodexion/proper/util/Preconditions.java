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

package com.github.rodexion.proper.util;

/**
 * <p>Null-checking utilities.</p>
 *
 * @author rodexion
 * @since 0.1
 */
public class Preconditions {
  /**
   * <p>Throw {@link NullPointerException} if the given value
   * is <code>null</code>.</p>
   *
   * @param name  Name to include in the error message (not-null)
   * @param value Value to check for <code>null</code> (maybe-null)
   * @param <T>   Value type
   * @return The value given (not-null)
   * @throws NullPointerException If the given value is null
   */
  public static final <T> T checkNotNull(String name, T value) {
    if (null == value) {
      throw new NullPointerException(name + " is null");
    }
    return value;
  }

  private Preconditions() {
  }
}
