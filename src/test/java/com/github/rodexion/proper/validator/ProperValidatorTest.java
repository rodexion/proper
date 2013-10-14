package com.github.rodexion.proper.validator;

import static org.fest.assertions.api.Assertions.assertThat;

import com.github.rodexion.proper.RuleUtils;
import org.fest.assertions.core.Condition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author rodexion
 * @since 0.1
 */
public class ProperValidatorTest {
  @Rule
  public TestRule tmpProps = RuleUtils.tmpSysProp(MyValidatedConstants.invalidIntProp);

  @Test
  public void test() {
    ProperValidator validator = ProperValidators.validator("com.github.rodexion");

    System.setProperty(MyValidatedConstants.invalidIntProp, "123123");
    ValidationResult withErrors = validator.validate();
    assertThat(withErrors.getValidationErrors())
            .hasSize(1)
            .haveExactly(1, new Condition<ValidationResult.Error>() {
              @Override
              public boolean matches(ValidationResult.Error value) {
                return value.getErrorMessage().equals("123123 is greater than 500") &&
                        value.getLocation().getLineNumber() == MyValidatedConstants.invalidIntLineNumber;
              }
            });

    System.setProperty(MyValidatedConstants.invalidIntProp, "123");
    assertThat(validator.validate().getValidationErrors()).hasSize(0);

    System.clearProperty(MyValidatedConstants.invalidIntProp);
    assertThat(validator.validate().getValidationErrors()).hasSize(0);
  }
}
