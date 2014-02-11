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
 * <p>Represents a nullable object, ala <code>Option</code> in Scala.</p>
 *
 * @param <T> Object type
 * @author rodexion
 * @since 0.1
 */
@EqualsAndHashCode
@ToString
public final class Opt<T> {
  private static final Opt<?> NONE = new Opt<>(null);
  private final T nullable;

  /**
   * <p>Represent an absence of object, i.e. <code>null</code> state.</p>
   *
   * @param <T> Object type
   * @return Opt object (not-null)
   */
  @SuppressWarnings("unchecked")
  public static <T> Opt<T> none() {
    return (Opt<T>) NONE;
  }

  /**
   * <p>Represents presence of an object, i.e. not <code>null</code> state.</p>
   *
   * @param value Object value (not-null)
   * @param <T>   Object type
   * @return Opt object (not-null)
   */
  public static <T> Opt<T> some(T value) {
    if (null == value) {
      throw new NullPointerException();
    }
    return of(value);
  }

  /**
   * <p>Creates an Opt object of a possibly-null value.</p>
   *
   * @param value Object value (maybe-null)
   * @param <T>   Object type
   * @return Opt object (not-null)
   */
  public static <T> Opt<T> of(T value) {
    return new Opt<>(value);
  }

  private Opt(T nullable) {
    this.nullable = nullable;
  }

  /**
   * <p>Checks if the object is absent.</p>
   *
   * @return <code>true</code> is object is absent, <code>false</code> otherwise.
   */
  public boolean isNone() {
    return nullable == null;
  }

  /**
   * <p>Retrieve the underlying object with a null-check.</p>
   *
   * @return The underlying object (not-null)
   * @throws NullPointerException If the object {@link #isNone()}.
   */
  public T get() {
    if (isNone()) {
      throw new NullPointerException();
    }
    return nullable;
  }

  /**
   * <p>Retrieve the underlying object if present, or the
   * given alternative if absent.</p>
   *
   * @param alternative Alternative object to return if this object {@link #isNone()}
   * @return This object, or alternative if this object {@link #isNone()}
   */
  public T getOr(T alternative) {
    if (isNone()) {
      return alternative;
    }
    return nullable;
  }

  /**
   * <p>Retrieve the underlying object if present, or <code>null</code>
   * if absent.</p>
   *
   * @return The underlying object (maybe-null)
   */
  public T getOrNull() {
    return nullable;
  }

  /**
   * <p>Convert this opt object to a set.</p>
   * <p>Set will contain exactly one item if the underlying object is
   * present, and will be empty otherwise.</p>
   *
   * @return Set representation of this opt object (not-null)
   */
  public Set<T> asSet() {
    if (isNone()) {
      Collections.emptySet();
    }
    return Collections.singleton(nullable);
  }
}
