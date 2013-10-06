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

import java.util.Collections;

/**
 * @author rodexion
 * @since 0.1
 */
public class PropertyBuilder<T> {
  private final String key;
  private final T defaultValue;
  private final Class<T> typeClass;
  private final Proper.Ty<T> info;
  private ConverterProvider converterProvider = Converters.voidConverterProvider();
  private Opt<Converter<T>> converter = Opt.none();
  private Validator<T> validator = Validators.voidValidator();
  private PropertyListener propertyListener = PropertyListeners.voidListener();

  @SuppressWarnings("unchecked")
  public PropertyBuilder(String key, T defaultValue) {
    this(key, defaultValue, (Class<T>) defaultValue.getClass());
  }

  public PropertyBuilder(String key, T defaultValue, Class<T> typeClass) {
    this.key = key;
    this.defaultValue = defaultValue;
    this.typeClass = typeClass;
    this.info = new Proper.Ty<>(key,
            typeClass,
            defaultValue,
            Collections.<String, Object>emptyMap());
  }

  public T value() {
    String value = System.getProperty(key);
    if (null == value) {
      propertyListener.notFound(info);
      return defaultValue;
    }
    Validator.Result validationBefore = validator.beforeConversion(value);
    if (!validationBefore.isOk()) {
      propertyListener.validationFailed(true, validationBefore, info);
      return defaultValue;
    }
    Converter<T> converter = getConverter();
    Converter.Result<T> result = converter.convert(value, info);
    if (!result.isOk()) {
      propertyListener.conversionFailed(result, info);
      return defaultValue;
    }
    Validator.Result validationAfter = validator.afterConversion(result.getValue());
    if (!validationAfter.isOk()) {
      propertyListener.validationFailed(false, validationAfter, info);
      return defaultValue;
    }
    return result.getValue();
  }

  public LazyValue<T> lazyValue() {
    return new LazyValue<T>() {
      @Override
      public T get() {
        return value();
      }
    };
  }

  public PropertyBuilder<T> converter(Converter<T> converter) {
    this.converter = Opt.some(converter);
    return this;
  }

  public PropertyBuilder<T> validator(Validator<T> validator) {
    this.validator = checkNotNull("validator", validator);
    return this;
  }

  public PropertyBuilder<T> converterProvider(ConverterProvider converterProvider) {
    this.converterProvider = checkNotNull("converterProvider", converterProvider);
    return this;
  }

  public PropertyBuilder<T> propertyListener(PropertyListener propertyListener) {
    this.propertyListener = checkNotNull("propertyListener", propertyListener);
    return this;
  }

  private Converter<T> getConverter() {
    if (!converter.isNone()) {
      return converter.get();
    }
    return converterProvider.getConverter(typeClass);
  }
}
