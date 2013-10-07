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
import com.github.rodexion.proper.util.PrimitiveTypeUtil;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rodexion
 * @since 0.1
 */
public class ConverterProviders {
  private static final ConverterProvider voidConverterProvider = new ConverterProvider() {
    @Override
    public <T> Converter<T> getConverter(Class<T> forType) {
      return Converters.voidConverter();
    }
  };

  private static final Map<Class<?>, Converter<?>> basicConverters = new HashMap<Class<?>, Converter<?>>() {
    {
      put(String.class, Converters.stringConverter());
      putPrim(Byte.class, Converters.byteConverter());
      putPrim(Short.class, Converters.shortConverter());
      putPrim(Integer.class, Converters.intConverter());
      putPrim(Long.class, Converters.longConverter());
      putPrim(Float.class, Converters.floatConverter());
      putPrim(Double.class, Converters.doubleConverter());
      putPrim(Character.class, Converters.characterConverter());
      put(BigDecimal.class, Converters.bigDecimalConverter());
      put(BigInteger.class, Converters.bigIntegerConverter());
      put(File.class, Converters.fileConverter());
      put(Path.class, Converters.pathConverter());
    }

    private <T> void putPrim(Class<T> typeClass, Converter<T> converter) {
      put(typeClass, converter);
      put(PrimitiveTypeUtil.getPrimitiveClass(typeClass), converter);
    }
  };

  public static ConverterProvider defaultConverterProvider() {
    return new DefaultConverterProvider();
  }

  public static ConverterProvider voidConverterProvider() {
    return voidConverterProvider;
  }

  public static Builder builder() {
    return new Builder();
  }

  private static final class DefaultConverterProvider implements ConverterProvider {
    @SuppressWarnings("unchecked")
    @Override
    public <T> Converter<T> getConverter(Class<T> forType) {
      if (null != forType) {
        Opt<Converter<T>> converterOpt = getNullableConverter(forType);
        if (converterOpt.isNone()) {
          if (Enum.class.isAssignableFrom(forType)) {
            //noinspection RedundantCast
            converterOpt = Opt.some((Converter<T>) (Converter) Converters.enumConverter());
          }
        }
        if (!converterOpt.isNone()) {
          if (converterOpt.get().canConvert(forType)) {
            return converterOpt.get();
          }
        }
      }
      return Converters.voidConverter();
    }

    @SuppressWarnings("unchecked")
    private <T> Opt<Converter<T>> getNullableConverter(Class<T> forType) {
      return Opt.of((Converter<T>) basicConverters.get(forType));
    }
  }

  public static class Builder {
    private final List<ConverterProvider> converterProviders = new ArrayList<>();
    private final List<Converter<?>> converters = new ArrayList<>();

    Builder() {
    }

    public Builder add(Converter<?> converter) {
      converters.add(checkNotNull("converter", converter));
      return this;
    }

    public Builder allFrom(ConverterProvider converterProvider) {
      converterProviders.add(checkNotNull("converterProvider", converterProvider));
      return this;
    }

    public ConverterProvider build() {
      return new ConverterProvider() {
        @SuppressWarnings("unchecked")
        @Override
        public <T> Converter<T> getConverter(Class<T> forType) {
          final List<Converter<T>> converterChain = new ArrayList<>();
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
            public Result<T> convert(String value, Proper.Info<T> info) {
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

  private ConverterProviders() {
  }
}
