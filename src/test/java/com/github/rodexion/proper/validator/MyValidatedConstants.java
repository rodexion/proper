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

  public static final int invalidIntLineNumber = 20;
  public static final String invalidIntProp = "test.invalid.int";
  public static final int invalidInt = MyProper.ty(invalidIntProp, 333, "An invalid int", Unit.Int)
          .validator(Validators.<Integer>comparableValidatorBuilder().max(500).build())
          .build().getValue();
}
