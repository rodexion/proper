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
 * <p>Provider of validators for a specified property conversion type.</p>
 *
 * @author rodexion
 * @since 0.1
 */
public interface ValidatorProvider {
  /**
   * <p>Retrieve validator for the given property conversion type, if any.</p>
   * <p>In case an appropriate validator is not found,
   * an instance of {@link com.github.rodexion.proper.Validators#voidValidator()} or
   * equivalent should be returned, instead of <code>null</code>.</p>
   *
   * @param forType Type to which the target property is to be converted to
   * @param <T>     Type to which the target property is to be converted to
   * @return Validator object instance (not-null)
   */
  <T> Validator<T> getValidator(Class<T> forType);
}
