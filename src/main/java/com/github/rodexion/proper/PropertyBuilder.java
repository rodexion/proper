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

import com.github.rodexion.proper.bus.InternalBuilderBus;
import com.github.rodexion.proper.bus.ProperLocation;

import java.util.Collections;
import java.util.Map;

/**
 * @author rodexion
 * @since 0.1
 */
public class PropertyBuilder<T> {
  private final String key;
  private final Class<T> typeClass;
  private final T defaultValue;
  private final ProperLocation location;
  private Converter<T> converter = Converters.voidConverter();
  private Validator<T> validator = Validators.voidValidator();
  private PropertyListener propertyListener = PropertyListeners.voidListener();
  private Map<String, Object> attributes = Collections.emptyMap();

  PropertyBuilder(String key, Class<T> typeClass, T defaultValue) {
    this.key = key;
    this.typeClass = typeClass;
    this.defaultValue = defaultValue;
    this.location = ProperLocation.getCurrentDeclarationLocation();
  }

  public PropertyBuilder<T> converter(Converter<T> converter) {
    this.converter = checkNotNull("converter", converter);
    return this;
  }

  public PropertyBuilder<T> converterProvider(ConverterProvider converterProvider) {
    this.converter = checkNotNull("converterProvider", converterProvider).getConverter(typeClass);
    return this;
  }

  public PropertyBuilder<T> validator(Validator<T> validator) {
    this.validator = checkNotNull("validator", validator);
    return this;
  }


  public PropertyBuilder<T> validatorProvider(ValidatorProvider validatorProvider) {
    this.validator = checkNotNull("validatorProvider", validatorProvider).getValidator(typeClass);
    return this;
  }

  public PropertyBuilder<T> propertyListener(PropertyListener propertyListener) {
    this.propertyListener = checkNotNull("propertyListener", propertyListener);
    return this;
  }

  public PropertyBuilder<T> attributes(Map<String, Object> attributes) {
    this.attributes = checkNotNull("attributes", attributes);
    return this;
  }

  public Proper.Ty<T> build() {
    Proper.Ty<T> property = new Proper.Ty<>(new Proper.Info<>(key, typeClass, defaultValue, attributes),
            converter, validator, propertyListener);
    InternalBuilderBus.firePropertyBuilt(property, location);
    return property;
  }
}
