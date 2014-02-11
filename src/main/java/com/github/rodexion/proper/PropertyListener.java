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
 * <p>Listener of property declaration, conversion and validation events.</p>
 *
 * @author rodexion
 * @since 0.1
 */
public interface PropertyListener {
  /**
   * <p>Triggered when property is not found upon retrieval.</p>
   *
   * @param key  Property key (not-null)
   * @param info Property meta data (not-null)
   */
  void notFound(String key, Proper.Info<?> info);

  /**
   * <p>Triggered when property value has failed to be validated, before value conversion step.</p>
   *
   * @param key             Property key (not-null)
   * @param value           Property value (maybe-null)
   * @param validationError Validation error message (not-null)
   * @param info            Property meta data (not-null)
   */
  void validationBeforeConversionFailed(String key, String value, String validationError, Proper.Info<?> info);

  /**
   * <p>Triggered when property value has failed to be converted.</p>
   *
   * @param key             Property key (not-null)
   * @param value           Property value (maybe-null)
   * @param conversionError Conversion error message (not-null)
   * @param info            Property meta data (not-null)
   */
  void conversionFailed(String key, String value, String conversionError, Proper.Info<?> info);

  /**
   * <p>Triggered when property value has failed to be validated, after value conversion step.</p>
   *
   * @param key             Property key (not-null)
   * @param value           Converted property value (maybe-null)
   * @param validationError Validation error message (not-null)
   * @param info            Property meta data (not-null)
   */
  void validationAfterConversionFailed(String key, Object value, String validationError, Proper.Info<?> info);

  /**
   * <p>Triggered when property value has failed to be validated, before value conversion step.</p>
   *
   * @param key            Property key (not-null)
   * @param stringValue    Property value before conversion (maybe-null)
   * @param convertedValue Converted property value (not-null)
   * @param info           Property meta data (not-null)
   */
  void success(String key, String stringValue, Object convertedValue, Proper.Info<?> info);
}
