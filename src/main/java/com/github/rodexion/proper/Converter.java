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
 * @author rodexion
 * @since 0.1
 */
public interface Converter<T> {
  @Data
  public static class Result<T> {
    private final T value;
    private final boolean ok;
    private final boolean skip;
    private final String errorMessage;

    public static final <T> Result<T> ok(T value) {
      return new Result<>(value, true, false, null);
    }

    public static final <T> Result<T> fail(String errorMessage) {
      return new Result<>(null, false, false, errorMessage);
    }

    public static final <T> Result<T> skip() {
      return new Result<>(null, false, true, null);
    }

    public Opt<T> asOpt() {
      return Opt.of(value);
    }

    public boolean isFailure() {
      return !ok && !skip;
    }
  }

  boolean canConvert(Class<?> type);

  Result<T> convert(String value, Proper.Info<T> info);
}
