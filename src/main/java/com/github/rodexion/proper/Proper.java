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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * @author rodexion
 * @since 0.1
 */
public class Proper {
  @Data
  public static final class Info<T> {
    private final String key;
    private final Class<T> type;
    private final T defaultValue;
    private final Map<String, Object> attributes;
  }

  @AllArgsConstructor
  @ToString
  public static final class Ty<T> implements LazyValue<T> {
    @Getter
    private final Proper.Info<T> info;
    private final Converter<T> converter;
    private final Validator<T> validator;
    private final PropertyListener propertyListener;

    public T getValue() {
      return getValue(propertyListener);
    }

    public T getValue(PropertyListener propertyListener) {
      String value = System.getProperty(info.getKey());
      if (null == value) {
        propertyListener.notFound(info);
        return info.getDefaultValue();
      }
      Validator.Result validationBefore = validator.beforeConversion(value, info);
      if (!validationBefore.isOk()) {
        propertyListener.validationBeforeConversionFailed(value, validationBefore.getErrorMessage(), info);
        return info.getDefaultValue();
      }
      Converter.Result<T> result = converter.convert(value, info);
      if (result.isFailure()) {
        propertyListener.conversionFailed(value, result.getErrorMessage(), info);
        return info.getDefaultValue();
      } else if (result.isSkip()) {
        //Maybe value does not need conversion
        if (info.getType().isAssignableFrom(String.class)) {
          return (T) value;
        } else {
          propertyListener.conversionFailed(value, "No suitable converter found", info);
        }
      }
      Validator.Result validationAfter = validator.afterConversion(result.getValue(), info);
      if (!validationAfter.isOk()) {
        propertyListener.validationAfterConversionFailed(result.getValue(), validationAfter.getErrorMessage(), info);
        return info.getDefaultValue();
      }
      propertyListener.success(value, result.getValue(), info);
      return result.getValue();
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> PropertyBuilder<T> tyBuilder(String key, T defaultValue) {
    checkNotNull("key", key);
    checkNotNull("defaultValue", defaultValue);
    return new PropertyBuilder<>(key, (Class<T>) defaultValue.getClass(), defaultValue)
            .converterProvider(ConverterProviders.defaultConverterProvider());
  }

  public static <T> PropertyBuilder<T> tyBuilder(String key, Class<T> typeClass) {
    return new PropertyBuilder<>(checkNotNull("key", key), checkNotNull("typeClass", typeClass), null);
  }

  private Proper() {
  }
}
