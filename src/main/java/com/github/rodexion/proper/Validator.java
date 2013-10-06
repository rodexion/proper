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
  @Data
  public static class Result {
    private final boolean ok;
    private final String errorMessage;

    public static final Result ok() {
      return new Result(true, null);
    }

    public static final Result fail(String errorMessage) {
      return new Result(false, errorMessage);
    }
  }

  Result beforeConversion(String value);

  Result afterConversion(T value);
}
