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

import static org.fest.assertions.api.Assertions.assertThat;

import org.fest.assertions.data.MapEntry;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rodexion
 * @since 0.1
 */
public class Examples {

  @Rule
  public TestRule tmpProps = RuleUtils.tmpSysProp("my.key", "my.key.1", "my.key.2");

  @SuppressWarnings("UnusedDeclaration")
  enum MyEnum {
    One, Two, Three;
  }

  /**
   * Your are recommended to define your own static property accessor.
   * <p/>
   * In the accessor you can set up your own default converters, validators
   * and property listeners.
   */
  public static class MyProper {
    static final List<String> messages = new ArrayList<>();

    public enum Unit {
      Int, Long, Float
    }

    public static <T> PropertyBuilder<T> ty(String key, T defaultValue, String description, Unit unit) {
      return ty(key, defaultValue, description, unit, (String[]) null);
    }

    public static <T> PropertyBuilder<T> ty(String key, T defaultValue, String description, Unit unit, String... dynamicKeySubs) {
      Map<String, Object> attrs = new HashMap<>();
      attrs.put("description", description);
      attrs.put("unit", unit);
      if (dynamicKeySubs != null) {
        attrs.put(Proper.ATTRIBUTE_DYNAMIC_KEY_SUBSTITUTIONS, dynamicKeySubs);
      }
      return Proper.tyBuilder(key, defaultValue)
              //Register a custom property listener
              .propertyListener(new PropertyListeners.BasePropertyListener() {
                @Override
                public void notFound(String key, Proper.Info<?> info) {
                  messages.add("Not found " + info.getKey() + " using default " + info.getDefaultValue());
                }

                @Override
                public void validationBeforeConversionFailed(String key, String value, String validationError, Proper.Info<?> info) {
                  messages.add(validationError);
                }

                @Override
                public void conversionFailed(String key, String value, String conversionError, Proper.Info<?> info) {
                  messages.add("Could not convert " + value + " to int");
                }

                @Override
                public void validationAfterConversionFailed(String key, Object value, String validationError, Proper.Info<?> info) {
                  messages.add(validationError);
                }
              })
                      //Setup custom converters
              .converterProvider(ConverterProviders.builder()
                      //Add converters for your custom types (btw, enums are converted by default!)
                      .add(new Converters.BaseConverter<MyEnum>(MyEnum.class) {
                        @Override
                        protected MyEnum doConvert(String key, String value, Proper.Info<MyEnum> info) {
                          return MyEnum.valueOf(value);
                        }
                      })
                              //Add all the default converters if (primitive types, BigDecimal, Enums, etc.)
                      .allFrom(ConverterProviders.defaultConverterProvider())
                      .build())
                      //Setup custom common validation
              .validatorProvider(ValidatorProviders.builder()
                      .add(new Validators.BaseValidator<MyEnum>(MyEnum.class) {
                        @Override
                        public Result afterConversion(String key, MyEnum value, Proper.Info<MyEnum> info) {
                          return value == MyEnum.One ? ok() : fail("value has to be 'One'");
                        }
                      }).build()
              )
              .attributes(attrs);
    }
  }

  @After
  public void clearMessages() {
    MyProper.messages.clear();
  }

  @Test
  public void basicUsage() {
    //Consider "my.key" not set
    Proper.Ty<Integer> intProp = MyProper.ty("my.key", 123, "My integer", MyProper.Unit.Int).build();
    assertThat(intProp.getInfo().getAttributes()).contains(
            MapEntry.entry("description", "My integer"),
            MapEntry.entry("unit", MyProper.Unit.Int));
    assertThat(intProp.getValue()).isEqualTo(123);
    assertThat(MyProper.messages.contains("Not found my.key using default 123"));
    MyProper.messages.clear();

    //Set it to a correct value
    System.setProperty("my.key", "234");
    assertThat(intProp.getValue()).isEqualTo(234);
    assertThat(MyProper.messages).isEmpty();

    //Set it to a wrong value
    System.setProperty("my.key", "xyz");
    assertThat(intProp.getValue()).isEqualTo(123);//default
    assertThat(MyProper.messages.contains("Could not convert xyz to int"));
  }

  @Test
  public void integerValidationExample() {
    Proper.Ty<Long> longProp = MyProper.ty("my.key", 123L, "My integer", MyProper.Unit.Int)
            .validator(Validators.<Long>comparableValidatorBuilder()
                    .min(0L)
                    .max(1000L)
                    .build())
            .build();

    //Try a valid value
    System.setProperty("my.key", "999");
    assertThat(longProp.getValue()).isEqualTo(999L);
    assertThat(MyProper.messages).isEmpty();

    //Try value out of valid scope
    System.setProperty("my.key", "1001");
    assertThat(longProp.getValue()).isEqualTo(123L);//default
    assertThat(MyProper.messages).contains("1001 is greater than 1000");
  }

  @Test
  public void requiredPropertyValidationExample() {
    Proper.Ty<Long> longProp = MyProper.ty("my.required.key", 123L, null/* key is required*/, MyProper.Unit.Int)
            .validator(Validators.<Long>requiredKeyValidator())
            .build();

    //Try a valid value
    System.setProperty("my.required.key", "999");
    assertThat(longProp.getValue()).isEqualTo(999L);
    assertThat(MyProper.messages).isEmpty();

    //Try not declaring the value
    System.clearProperty("my.required.key");
    assertThat(longProp.getValue()).isEqualTo(123L);//default
    assertThat(MyProper.messages)
            .contains("Not found my.required.key using default 123")
            .contains("Required property 'my.required.key' was not found.");
  }

  @Test
  public void complexPropertyExample() {
    System.setProperty("my.key.1", "1");
    System.setProperty("my.key.2", "2");
    Proper.Ty<Long> complexLongProp = MyProper.<Long>ty("my.key.{0}", 999L, "Complex property", MyProper.Unit.Long)
            .build();
    assertThat(complexLongProp.getValue(1)).isEqualTo(1L);
    assertThat(complexLongProp.getValue(2)).isEqualTo(2L);
    assertThat(complexLongProp.getValue(2, 3)).isEqualTo(2L);

    assertThat(complexLongProp.getValue()).isEqualTo(999L);
    assertThat(complexLongProp.getValue(3)).isEqualTo(999L);
    assertThat(complexLongProp.getValue(3, 4)).isEqualTo(999L);
  }
}
