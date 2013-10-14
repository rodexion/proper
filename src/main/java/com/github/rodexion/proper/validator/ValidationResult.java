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
