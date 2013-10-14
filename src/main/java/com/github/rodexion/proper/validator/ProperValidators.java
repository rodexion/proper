package com.github.rodexion.proper.validator;

import static com.github.rodexion.proper.util.Preconditions.checkNotNull;

import com.github.rodexion.proper.Proper;
import com.github.rodexion.proper.PropertyListeners;
import com.github.rodexion.proper.scanner.ProperDecl;
import com.github.rodexion.proper.scanner.ProperScanner;
import com.github.rodexion.proper.scanner.ProperScanners;
import com.github.rodexion.proper.util.Opt;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rodexion
 * @since 0.1
 */
public class ProperValidators {
  public static ProperValidator validator(String basePackage) {
    ProperScanner scanner = ProperScanners.scanner(checkNotNull("basePackage", basePackage));
    return new DefaultProperValidator(scanner);
  }

  @AllArgsConstructor
  private static final class DefaultProperValidator implements ProperValidator {
    private final ProperScanner scanner;

    @Override
    public ValidationResult validate() {
      List<ValidationResult.Error> errors = new ArrayList<>();
      for (ProperDecl properDecl : scanner.scan().getDeclarations()) {
        ValidationErrorCapture errorCapture = new ValidationErrorCapture();
        properDecl.getProperty().getValue(errorCapture);
        if (errorCapture.hasError) {
          errors.add(new ValidationResult.Error(
                  properDecl.getProperty(),
                  properDecl.getLocation(),
                  errorCapture.validationError.get()));
        }
      }
      return new ValidationResult(errors);
    }

    private static final class ValidationErrorCapture extends PropertyListeners.BasePropertyListener {
      Opt<String> inputValue = Opt.none();
      Opt<String> validationError = Opt.none();
      boolean hasError = false;

      @Override
      public void validationBeforeConversionFailed(String value, String validationError, Proper.Info<?> info) {
        validationError(value, validationError);
      }

      @Override
      public void conversionFailed(String value, String conversionError, Proper.Info<?> info) {
        validationError(value, conversionError);
      }

      @Override
      public void validationAfterConversionFailed(Object value, String validationError, Proper.Info<?> info) {
        validationError(value, validationError);
      }

      private void validationError(Object value, String validationError) {
        hasError = true;
        inputValue = Opt.some(String.valueOf(value));
        this.validationError = Opt.some(validationError);
      }
    }
  }
}
