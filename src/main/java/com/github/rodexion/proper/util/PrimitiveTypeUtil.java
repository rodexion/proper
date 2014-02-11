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

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Primitive type utilities.</p>
 *
 * @author rodexion
 * @since 0.1
 */
public class PrimitiveTypeUtil {
  private static final Map<Class<?>, Class<?>> primitiveCounterparts = new HashMap<Class<?>, Class<?>>() {{
    put(Byte.class, byte.class);
    put(Short.class, short.class);
    put(Integer.class, int.class);
    put(Long.class, long.class);
    put(Float.class, float.class);
    put(Double.class, double.class);
    put(Character.class, char.class);
  }};

  /**
   * <p>Return the primitive counterpart class for the given boxed type.</p>
   *
   * @param boxedClass Boxed type class (not-null)
   * @param <T>        Type
   * @return Primitive counterpart class, or <code>null</code>
   *         if such class does not exist. (maybe-null)
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> getPrimitiveClass(Class<T> boxedClass) {
    return (Class<T>) primitiveCounterparts.get(boxedClass);
  }

  private PrimitiveTypeUtil() {
  }
}
