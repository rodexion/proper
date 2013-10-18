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

import lombok.*;

import java.util.Map;

/**
 * <p>Static factory for properties. Use {@link Proper#tyBuilder(String, Class)}
 * to create and configure property instances.</p>
 *
 * @author rodexion
 * @since 0.1
 */
public class Proper {
  /**
   * <p>Basic property information holder</p>
   * <p>Note: Hash code and equals implementations are only based on the
   * {@link #key} value.</p>
   *
   * @param <T>
   */
  @Data
  @EqualsAndHashCode(of = {"key"})
  public static final class Info<T> {
    /**
     * <p>Unique property key (not-null)</p>
     */
    private final String key;
    /**
     * <p>Property type (not-null)</p>
     */
    private final Class<T> type;
    /**
     * <p>Default value used when property is not set, or conversion/validation has failed (maybe null)</p>
     */
    private final T defaultValue;
    /**
     * <p>Custom property attributes (not-null)</p>
     */
    private final Map<String, Object> attributes;
  }

  /**
   * <p>Note: Hash code and equals implementations are only based on the {@link #info},
   * which in turn is only based on its {@link Info#key} field.</p>
   *
   * @param <T>
   */
  @AllArgsConstructor
  @ToString
  @EqualsAndHashCode(of = {"info"})
  public static final class Ty<T> implements LazyValue<T> {
    /**
     * <p>Basic property information (not-null)</p>
     */
    @Getter
    private final Proper.Info<T> info;
    private final Converter<T> converter;
    private final Validator<T> validator;
    private final PropertyListener propertyListener;

    /**
     * <p>Retrieves the current system property value, after applying
     * any conversion and/or validation rules declared (maybe null)</p>
     * <p>Failure to find the appropriate converter for this
     * property type, or to validate the property will return the
     * default value (see {@link com.github.rodexion.proper.Proper.Info#getDefaultValue()})</p>
     *
     * @return Current value for this system property, or the default value
     */
    public T getValue() {
      return getValue(propertyListener);
    }

    /**
     * <p>Retrieves the current system property value, after applying
     * any conversion and/or validation rules declared (maybe null)</p>
     * <p>Failure to find the appropriate converter for this
     * property type, or to validate the property will return the
     * default value (see {@link com.github.rodexion.proper.Proper.Info#getDefaultValue()})</p>
     *
     * @param propertyListener property listener to use
     * @return Current value for this system property, or the default value
     */
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

  /**
   * <p>Initialises basic system property builder, pre-configured with
   * the default converter set (see {@link com.github.rodexion.proper.ConverterProviders#defaultConverterProvider()}</p>
   * <p/>
   * <p>If you wish to use a <code>null</code> for the default value, use the {@link #tyBuilder(String, Class)} static
   * constructor</p>
   *
   * @param key          Unique system property key (not null)
   * @param defaultValue Default value to fall back to on missing property or conversion/validation failure (not null)
   * @param <T>          Property type
   * @return Property builder object
   */
  @SuppressWarnings("unchecked")
  public static <T> PropertyBuilder<T> tyBuilder(String key, T defaultValue) {
    checkNotNull("key", key);
    checkNotNull("defaultValue", defaultValue);
    return new PropertyBuilder<>(key, (Class<T>) defaultValue.getClass(), defaultValue)
            .converterProvider(ConverterProviders.defaultConverterProvider());
  }

  /**
   * <p>Initialises basic system property builder, with a <code>null</code> default value, pre-configured with
   * the default converter set (see {@link com.github.rodexion.proper.ConverterProviders#defaultConverterProvider()}</p>
   * <p/>
   *
   * @param key       Unique system property key (not null)
   * @param typeClass Property type class (not null)
   * @param <T>       Property type
   * @return Property builder object
   */
  public static <T> PropertyBuilder<T> tyBuilder(String key, Class<T> typeClass) {
    return new PropertyBuilder<>(checkNotNull("key", key), checkNotNull("typeClass", typeClass), null);
  }

  private Proper() {
  }
}
