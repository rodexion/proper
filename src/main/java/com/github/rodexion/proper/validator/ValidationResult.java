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

package com.github.rodexion.proper.validator;

import com.github.rodexion.proper.Proper;
import com.github.rodexion.proper.bus.ProperLocation;
import lombok.Data;

import java.util.List;

/**
 * @author rodexion
 * @since 0.1
 */
@Data
public class ValidationResult {
  private final List<Error> validationErrors;

  @Data
  public static final class Error {
    private final Proper.Ty<?> property;
    private final ProperLocation location;
    private final String errorMessage;
  }
}
