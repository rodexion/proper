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

import com.github.rodexion.proper.util.Opt;
import lombok.Data;

/**
 * * <p>Used for converting properties from String type to
 * any other type.</p>
 *
 * @param <T> Conversion target type
 */
public interface Converter<T> {
  @Data
  public static class Result<T> {
    private final T value;
    private final boolean ok;
    private final boolean skip;
    private final String errorMessage;

    /**
     * <p>A successful result</p>
     *
     * @param value Converted value (maybe-null)
     * @param <T>   Conversion target type
     * @return Result object (not-null)
     */
    public static final <T> Result<T> ok(T value) {
      return new Result<>(value, true, false, null);
    }

    /**
     * <p>An unsuccessful result</p>
     *
     * @param errorMessage Error that had caused the failure (not-null)
     * @param <T>          Conversion target type
     * @return Result object (not-null)
     */
    public static final <T> Result<T> fail(String errorMessage) {
      return new Result<>(null, false, false, errorMessage);
    }

    /**
     * <p>An indication that the current converter is not in charge
     * of handling this kind of property conversion. Conversion should
     * be delegated to another converter, if available.</p>
     *
     * @param <T> Conversion target type
     * @return Result object (not-null)
     */
    public static final <T> Result<T> skip() {
      return new Result<>(null, false, true, null);
    }

    /**
     * <p>Get converted value as opt object.</p>
     *
     * @return Converted value as opt object (not-null)
     */
    public Opt<T> asOpt() {
      return Opt.of(value);
    }

    /**
     * <p>Check if the current result object signifies a failure.</p>
     *
     * @return <code>true</code> if result is a failure, <code>false</code> otherwise.
     */
    public boolean isFailure() {
      return !ok && !skip;
    }
  }

  boolean canConvert(Class<?> type);

  Result<T> convert(String key, String value, Proper.Info<T> info);
}
