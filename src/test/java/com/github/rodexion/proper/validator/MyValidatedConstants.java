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

import static com.github.rodexion.proper.Examples.MyProper;
import static com.github.rodexion.proper.Examples.MyProper.Unit;

import com.github.rodexion.proper.Validators;
import com.github.rodexion.proper.annotations.ProperScannable;

/**
 * @author rodexion
 * @since 0.1
 */
@ProperScannable
public class MyValidatedConstants {
  public static final int validInt = MyProper.ty("test.valid.int", 888, "A valid int", Unit.Int)
          .build().getValue();

  public static final int invalidIntLineNumber = 40;
  public static final String invalidIntProp = "test.invalid.int";
  public static final int invalidInt = MyProper.ty(invalidIntProp, 333, "An invalid int", Unit.Int)
          .validator(Validators.<Integer>comparableValidatorBuilder().max(500).build())
          .build().getValue();
}
