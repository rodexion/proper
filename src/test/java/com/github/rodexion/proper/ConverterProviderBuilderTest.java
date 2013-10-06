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

import org.junit.Test;

import java.util.Collections;

/**
 * @author rodexion
 * @since 0.1
 */
public class ConverterProviderBuilderTest {
  @Test
  public void empty() {
    assertThat(ConverterProviderBuilder.builder().build()
            .getConverter(Integer.class).convert("any", property("key", Integer.class)))
            .isEqualTo(Converter.Result.<Integer>skip());
  }

  @Test
  public void freeConverter() {
    assertThat(ConverterProviderBuilder.builder()
            .converter(new Converter<String>() {
              @Override
              public boolean canConvert(Class<?> type) {
                return true;
              }

              @Override
              public Result<String> convert(String value, Proper.Ty<String> info) {
                return Result.ok("ok");
              }
            })
            .build()
            .getConverter(String.class).convert("any", property("key", String.class)))
            .isEqualTo(Converter.Result.ok("ok"));
  }

  @Test
  public void mappedConverter() {
    ConverterProvider cp = ConverterProviderBuilder.builder()
            .converter(Integer.class, Converters.intConverter())
            .converter(new Converter<Object>() {
              @Override
              public boolean canConvert(Class<?> type) {
                return true;
              }

              @Override
              public Result<Object> convert(String value, Proper.Ty<Object> info) {
                return Result.fail("fallback");
              }
            }).build();
    assertThat(cp.getConverter(Integer.class).convert("123", property("key", Integer.class)))
            .isEqualTo(Converter.Result.ok(123));
    assertThat(cp.getConverter(String.class).convert("123", property("key", String.class)))
            .isEqualTo(Converter.Result.<String>fail("fallback"));
  }

  @Test
  public void compoundConverter() {
    ConverterProvider cp = ConverterProviderBuilder.builder()
            .convertersFrom(Converters.defaultConverterProvider())
            .converter(new Converter<Object>() {
              @Override
              public boolean canConvert(Class<?> type) {
                return true;
              }

              @Override
              public Result<Object> convert(String value, Proper.Ty<Object> info) {
                return Result.fail("fallback");
              }
            }).build();
    assertThat(cp.getConverter(Float.class).convert("12.3", property("key", Float.class)))
            .isEqualTo(Converter.Result.ok(12.3f));
    assertThat(cp.getConverter(Object.class).convert("oops", property("key", Object.class)))
            .isEqualTo(Converter.Result.fail("fallback"));
  }

  private static <T> Proper.Ty<T> property(String key, Class<T> type) {
    return new Proper.Ty<>(key, type, null, Collections.<String, Object>emptyMap());
  }
}
