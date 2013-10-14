package com.github.rodexion.proper.scanner;

import com.github.rodexion.proper.Examples;
import com.github.rodexion.proper.annotations.ProperScannable;

/**
 * @author rodexion
 * @since 0.1
 */
public class MyPropeties2 {
  public final static int floatPropLineNumber = 19;

  /**
   * a random
   * javadoc to move the line number
   * down a little
   */
  @ProperScannable
  private static final float floatProp = Examples.MyProper.ty("test.float.prop", 123.234f, "Float property",
          Examples.MyProper.Unit.Float).build().getValue();
}
