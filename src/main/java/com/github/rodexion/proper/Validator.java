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

import lombok.Data;

/**
 * @author rodexion
 * @since 0.1
 */
public interface Validator<T> {

  /**
   * <p>Representation of validation outcome.</p>
   */
  @Data
  public static class Result {
    private final boolean ok;
    private final String errorMessage;

    /**
     * <p>Creates a validation success result object.</p>
     *
     * @return Validation result object (not-null)
     */
    public static final Result ok() {
      return new Result(true, null);
    }

    /**
     * <p>Creates a validation failure result object.</p>
     *
     * @param errorMessage Validation error message (not-null)
     * @return Validation result object (not-null)
     */
    public static final Result fail(String errorMessage) {
      return new Result(false, errorMessage);
    }
  }

  /**
   * <p>Return <code>true</code> if this validator is to be applied
   * against properties of type specified by the method argument.</p>
   *
   * @param type Property type
   * @return <code>true</code> if this validator will be applied,
   *         <code>false</code> otherwise.
   */
  boolean canValidate(Class<?> type);

  /**
   * <p>Invoked before any of the converters are applied</p>
   *
   * @param key   Key being validated (not-null)
   * @param value System property value retrieved (maybe-null)
   * @param info  Associated property info object (not-null)
   * @return Validation result
   */
  Result beforeConversion(String key, String value, Proper.Info<T> info);

  /**
   * <p>Invoked after all of the converters are applied</p>
   *
   * @param key   Key being validated (not-null)
   * @param value System property value retrieved (maybe-null)
   * @param info  Associated property info object (not-null)
   * @return Validation result
   */
  Result afterConversion(String key, T value, Proper.Info<T> info);
}
