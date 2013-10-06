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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rodexion
 * @since 0.1
 */
public class ConverterProviderBuilder {
  private final List<ConverterProvider> converterProviders = new ArrayList<>();
  private final List<Converter<?>> converters = new ArrayList<>();
  private final Map<Class<?>, Converter<?>> mappedConverters = new HashMap<>();

  public static ConverterProviderBuilder builder() {
    return new ConverterProviderBuilder();
  }

  public <T> ConverterProviderBuilder converter(Class<T> forType, Converter<T> converter) {
    mappedConverters.put(forType, converter);
    return this;
  }

  public ConverterProviderBuilder converter(Converter<?> converter) {
    converters.add(converter);
    return this;
  }

  public ConverterProviderBuilder convertersFrom(ConverterProvider converterProvider) {
    converterProviders.add(converterProvider);
    return this;
  }

  public ConverterProvider build() {
    return new ConverterProvider() {
      @SuppressWarnings("unchecked")
      @Override
      public <T> Converter<T> getConverter(Class<T> forType) {
        final List<Converter<T>> converterChain = new ArrayList<>();
        Converter<?> mappedConverter = mappedConverters.get(forType);
        if (null != mappedConverter && mappedConverter.canConvert(forType)) {
          converterChain.add((Converter<T>) mappedConverter);
        }
        for (ConverterProvider converterProvider : converterProviders) {
          Converter<T> converter = converterProvider.getConverter(forType);
          if (converter.canConvert(forType)) {
            converterChain.add(converter);
          }
        }
        for (Converter<?> converter : converters) {
          if (converter.canConvert(forType)) {
            converterChain.add((Converter<T>) converter);
          }
        }
        return new Converter<T>() {
          @Override
          public boolean canConvert(Class<?> type) {
            return !converterChain.isEmpty();
          }

          @Override
          public Result<T> convert(String value, Proper.Ty<T> info) {
            for (Converter<T> converter : converterChain) {
              Result<?> result = converter.convert(value, info);
              if (!result.isSkip()) {
                return (Result<T>) result;
              }
            }
            return Result.skip();
          }
        };
      }
    };
  }
}
