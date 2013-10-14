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

import static org.fest.assertions.api.Assertions.assertThat;

import com.github.rodexion.proper.SeparateClassLoaderTestRunner;
import com.github.rodexion.proper.TestConstants;
import org.fest.assertions.core.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * <p>Warning, fragile test! This test must be executed in its own class loader, to make
 * sure previous tests that may have not pre-populated internal bus with properties,
 * do not affect the execution of this test.</p>
 *
 * @author rodexion
 * @since 0.1
 */
@RunWith(SeparateClassLoaderTestRunner.class)
public class NonAnnotatedFieldsScanTest {
  @Test
  public void propertiesNotMarkedWithScannableCanBeDetectedWithFullValidaition() {
    ValidationResult withErrors = ProperValidators.validateAll(TestConstants.PROPER_BASE_PACKAGE).validate();
    System.setProperty(MyValidatedConstants.invalidIntProp, "123");
    assertThat(withErrors.getValidationErrors())
            .hasSize(1)
            .haveExactly(1, new Condition<ValidationResult.Error>() {
              @Override
              public boolean matches(ValidationResult.Error value) {
                return value.getErrorMessage().equals(
                        String.format("Property 'test.double.notMarked' declared in MyValidationConstants2.java@%d is missing @ProperScannable annotation.",
                                MyValidationConstants2.propNotMarkedLineNumber)) &&
                        value.getLocation().getLineNumber() == MyValidationConstants2.propNotMarkedLineNumber;

              }
            });
  }
}
