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

/**
 * <p>Collection of default property listener implementations.</p>
 *
 * @author rodexion
 * @since 0.1
 */
public class PropertyListeners {
  private static final PropertyListener voidListener = new BasePropertyListener();

  /**
   * <p>Creates a property listener that does nothing.</p>
   *
   * @return Property listener object (not-null)
   */
  public static PropertyListener voidListener() {
    return voidListener;
  }

  /**
   * <p>Base class for creating custom property listeners.</p>
   */
  public static class BasePropertyListener implements PropertyListener {

    @Override
    public void notFound(String key, Proper.Info<?> info) {
    }

    @Override
    public void validationBeforeConversionFailed(String key, String value, String validationError, Proper.Info<?> info) {
    }

    @Override
    public void conversionFailed(String key, String value, String conversionError, Proper.Info<?> info) {
    }

    @Override
    public void validationAfterConversionFailed(String key, Object value, String validationError, Proper.Info<?> info) {
    }

    @Override
    public void success(String key, String stringValue, Object convertedValue, Proper.Info<?> info) {
    }
  }

  private PropertyListeners() {
  }
}
