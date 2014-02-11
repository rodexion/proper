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

import com.github.rodexion.proper.util.Opt;

/**
 * <p>A collection of default validator implementations.</p>
 *
 * @author rodexion
 * @since 0.1
 */
public class Validators {

  /**
   * <p>Base class for creating custom validator objects.</p>
   *
   * @param <T> Property conversion target type
   */
  public abstract static class BaseValidator<T> implements Validator<T> {
    private final Class<T> typeClass;

    public BaseValidator(Class<T> typeClass) {
      this.typeClass = checkNotNull("typeClass", typeClass);
    }

    public final Result ok() {
      return Result.ok();
    }

    public final Result fail(String errorMessage) {
      return Result.fail(errorMessage);
    }

    @Override
    public boolean canValidate(Class<?> type) {
      return typeClass.equals(type);
    }

    @Override
    public Result beforeConversion(String key, String value, Proper.Info<T> info) {
      return ok();
    }

    @Override
    public Result afterConversion(String key, T value, Proper.Info<T> info) {
      return ok();
    }
  }

  private static final Validator<?> voidValidator = new Validator<Object>() {
    @Override
    public boolean canValidate(Class<?> type) {
      return false;
    }

    @Override
    public Result beforeConversion(String key, String value, Proper.Info<Object> info) {
      return Result.ok();
    }

    @Override
    public Result afterConversion(String key, Object value, Proper.Info<Object> info) {
      return Result.ok();
    }
  };

  /**
   * <p>Creates an instance of validator that always succeeds.</p>
   *
   * @param <T> Property conversion target type
   * @return Validator object (not-null)
   */
  @SuppressWarnings("unchecked")
  public static <T> Validator<T> voidValidator() {
    return (Validator<T>) voidValidator;
  }

  /**
   * <p>Validator which makes sure the given property is declared,
   * i.e. the corresponding value in not <code>null</code>.</p>
   *
   * @param <T> Property conversion target type
   * @return Validator instance (not-null)
   */
  public static <T> Validator<T> requiredKeyValidator() {
    return new Validator<T>() {
      @Override
      public boolean canValidate(Class<?> type) {
        return true;
      }

      @Override
      public Result beforeConversion(String key, String value, Proper.Info<T> info) {
        if (null == value) {
          return Result.fail("Required property '" + key + "' was not found.");
        }
        return Result.ok();
      }

      @Override
      public Result afterConversion(String key, T value, Proper.Info<T> info) {
        return Result.ok();
      }
    };
  }

  /**
   * <p>A tool for creating validator for values that have upper and/or lower
   * boundaries. E.g. Make sure <code>0 &lt; value &lt; 100</code></p>
   *
   * @param <T> Property conversion target type
   * @return Validator builder object (not-null)
   */
  public static <T extends Comparable<T>> ComparableValidatorBuilder<T> comparableValidatorBuilder() {
    return new ComparableValidatorBuilder<>();
  }

  /**
   * <p>A tool for creating validator for values that have upper and/or lower
   * boundaries. E.g. Make sure <code>0 &lt; value &lt; 100</code></p>
   *
   * @param <T> Property conversion target type
   */
  public static final class ComparableValidatorBuilder<T extends Comparable<T>> {
    private Opt<T> min = Opt.none();
    private Opt<T> max = Opt.none();

    /**
     * <p>Makes sure validated values are greater than or equal to
     * the given <code>min</code> value.</p>
     *
     * @param min Lower boundary value (not-null)
     * @return this builder object (not-null)
     */
    public ComparableValidatorBuilder<T> min(T min) {
      this.min = Opt.some(checkNotNull("min", min));
      return this;
    }

    /**
     * <p>Makes sure validated values are less than or equal to
     * the given <code>max</code> value.</p>
     *
     * @param max Upper boundary value (not-null)
     * @return this builder object (not-null)
     */
    public ComparableValidatorBuilder<T> max(T max) {
      this.max = Opt.some(checkNotNull("max", max));
      return this;
    }

    /**
     * <p>Create an instance of validator that will check for
     * the specified upper/lower boundaries.</p>
     *
     * @return Validator object (not-null)
     */
    public Validator<T> build() {
      return new Validator<T>() {
        @Override
        public boolean canValidate(Class<?> type) {
          return Comparable.class.isAssignableFrom(type);
        }

        @Override
        public Result beforeConversion(String key, String value, Proper.Info<T> info) {
          return Result.ok();
        }

        @Override
        public Result afterConversion(String key, T value, Proper.Info<T> info) {
          if (!min.isNone() && value.compareTo(min.get()) < 0) {
            return Result.fail(value + " is less than " + min.get());
          }
          if (!max.isNone() && value.compareTo(max.get()) > 0) {
            return Result.fail(value + " is greater than " + max.get());
          }
          return Result.ok();
        }
      };
    }
  }

  private Validators() {
  }
}
