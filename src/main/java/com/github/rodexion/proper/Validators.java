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

/**
 * @author rodexion
 * @since 0.1
 */
public class Validators {
  public abstract static class BaseValidator<T> implements Validator<T> {
    public final Result ok() {
      return Result.ok();
    }

    public final Result fail(String errorMessage) {
      return Result.fail(errorMessage);
    }

    @Override
    public Result beforeConversion(String value) {
      return ok();
    }

    @Override
    public Result afterConversion(T value) {
      return ok();
    }
  }

  private static final Validator<?> voidValidator = new BaseValidator<Object>() {
  };

  public static <T> Validator<T> voidValidator() {
    return (Validator<T>) voidValidator;
  }
}
