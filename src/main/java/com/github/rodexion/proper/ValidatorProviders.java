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

import static com.github.rodexion.proper.util.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rodexion
 * @since 0.1
 */
public class ValidatorProviders {
  private static final ValidatorProvider voidValidatorProvider = new ValidatorProvider() {
    @Override
    public <T> Validator<T> getValidator(Class<T> forType) {
      return Validators.voidValidator();
    }
  };

  public static ValidatorProvider voidValidatorProvider() {
    return voidValidatorProvider;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final List<ValidatorProvider> validatorProviders = new ArrayList<>();
    private final List<Validator<?>> validators = new ArrayList<>();

    Builder() {
    }

    public Builder add(Validator<?> validator) {
      validators.add(checkNotNull("validator", validator));
      return this;
    }

    public Builder allFrom(ValidatorProvider validatorProvider) {
      validatorProviders.add(checkNotNull("validatorProvider", validatorProvider));
      return this;
    }

    public ValidatorProvider build() {
      return new ValidatorProvider() {
        @SuppressWarnings("unchecked")
        @Override
        public <T> Validator<T> getValidator(Class<T> forType) {
          final List<Validator<T>> validatorChain = new ArrayList<>();
          for (ValidatorProvider validatorProvider : validatorProviders) {
            Validator<T> validator = validatorProvider.getValidator(forType);
            if (validator.canValidate(forType)) {
              validatorChain.add(validator);
            }
          }
          for (Validator<?> validator : validators) {
            if (validator.canValidate(forType)) {
              validatorChain.add((Validator<T>) validator);
            }
          }
          return new Validator<T>() {
            @Override
            public boolean canValidate(Class<?> type) {
              return !validatorChain.isEmpty();
            }

            @Override
            public Result beforeConversion(String value, Proper.Info<T> info) {
              for (Validator<T> validator : validatorChain) {
                Result result = validator.beforeConversion(value, info);
                if (!result.isOk()) {
                  return result;
                }
              }
              return Result.ok();
            }

            @Override
            public Result afterConversion(T value, Proper.Info<T> info) {
              for (Validator<T> validator : validatorChain) {
                Result result = validator.afterConversion(value, info);
                if (!result.isOk()) {
                  return result;
                }
              }
              return Result.ok();
            }
          };
        }
      };
    }
  }

  private ValidatorProviders() {
  }
}
