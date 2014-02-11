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

import com.github.rodexion.proper.RuleUtils;
import com.github.rodexion.proper.TestConstants;
import org.fest.assertions.core.Condition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.text.MessageFormat;

/**
 * @author rodexion
 * @since 0.1
 */
public class ProperValidatorTest {
  @Rule
  public TestRule tmpProps = RuleUtils.tmpSysProp(MyValidatedConstants.invalidIntProp);

  @Test
  public void allPropertiesThatDoNotPassValidationAreGathered() {
    ProperValidators.validateAll(TestConstants.PROPER_BASE_PACKAGE).validate();
    ProperValidator validator = ProperValidators.validator(TestConstants.PROPER_BASE_PACKAGE);

    System.setProperty(MyValidatedConstants.invalidIntProp, "123123");
    ValidationResult withErrors = validator.validate();
    assertThat(withErrors.getValidationErrors()).haveExactly(1, intPropCondition());

    System.setProperty(MyValidatedConstants.invalidIntProp, "123");
    assertThat(validator.validate().getValidationErrors()).doNotHave(intPropCondition());

    System.clearProperty(MyValidatedConstants.invalidIntProp);
    assertThat(validator.validate().getValidationErrors()).doNotHave(intPropCondition());

    final String complexProp1 = MessageFormat.format(MyValidatedConstants.complexProp, "test1");
    final String complexProp2 = MessageFormat.format(MyValidatedConstants.complexProp, "test2");
    System.setProperty(complexProp1, "complex-value");
    assertThat(validator.validate().getValidationErrors()).haveExactly(1, new Condition<ValidationResult.Error>() {
      @Override
      public boolean matches(ValidationResult.Error value) {
        as("sorry no prop found: " + complexProp2);
        return value.getErrorMessage().equals("sorry no prop found: " + complexProp2);
      }
    });
  }

  private Condition<ValidationResult.Error> intPropCondition() {
    return new Condition<ValidationResult.Error>() {
      @Override
      public boolean matches(ValidationResult.Error value) {
        return value.getErrorMessage().equals("123123 is greater than 500") &&
                value.getLocation().getLineNumber() == MyValidatedConstants.invalidIntLineNumber;
      }
    };
  }
}
