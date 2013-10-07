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

import java.util.Collections;

/**
 * @author rodexion
 * @since 0.1
 */
public class PropertyBuilder<T> {
  private final Proper.Info<T> info;
  private Converter<T> converter = Converters.voidConverter();
  private Validator<T> validator = Validators.voidValidator();
  private PropertyListener propertyListener = PropertyListeners.voidListener();

  PropertyBuilder(String key, Class<T> typeClass, T defaultValue) {
    this.info = new Proper.Info<>(key,
            typeClass,
            defaultValue,
            Collections.<String, Object>emptyMap());
  }

  public PropertyBuilder<T> converter(Converter<T> converter) {
    this.converter = checkNotNull("converter", converter);
    return this;
  }

  public PropertyBuilder<T> converterProvider(ConverterProvider converterProvider) {
    this.converter = checkNotNull("converterProvider", converterProvider).getConverter(info.getType());
    return this;
  }

  public PropertyBuilder<T> validator(Validator<T> validator) {
    this.validator = checkNotNull("validator", validator);
    return this;
  }


  public PropertyBuilder<T> validatorProvider(ValidatorProvider validatorProvider) {
    this.validator = checkNotNull("validatorProvider", validatorProvider).getValidator(info.getType());
    return this;
  }

  public PropertyBuilder<T> propertyListener(PropertyListener propertyListener) {
    this.propertyListener = checkNotNull("propertyListener", propertyListener);
    return this;
  }

  public Proper.Ty<T> build() {
    return new Proper.Ty<T>(info, converter, validator, propertyListener);
  }
}
