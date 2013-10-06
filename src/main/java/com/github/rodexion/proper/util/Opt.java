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

package com.github.rodexion.proper.util;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.Set;

/**
 * @author rodexion
 * @since 0.1
 */
@EqualsAndHashCode
@ToString
public final class Opt<T> {
  private static final Opt<?> NONE = new Opt<>(null);
  private final T nullable;

  @SuppressWarnings("unchecked")
  public static <T> Opt<T> none() {
    return (Opt<T>) NONE;
  }

  public static <T> Opt<T> some(T value) {
    if (null == value) {
      throw new NullPointerException();
    }
    return of(value);
  }

  public static <T> Opt<T> of(T value) {
    return new Opt<>(value);
  }

  private Opt(T nullable) {
    this.nullable = nullable;
  }

  public boolean isNone() {
    return nullable == null;
  }

  public T get() {
    if (isNone()) {
      throw new NullPointerException();
    }
    return nullable;
  }

  public T getOr(T alternative) {
    if (isNone()) {
      return alternative;
    }
    return nullable;
  }

  public T getOrNull() {
    return nullable;
  }

  public Set<T> asSet() {
    if (isNone()) {
      Collections.emptySet();
    }
    return Collections.singleton(nullable);
  }
}
