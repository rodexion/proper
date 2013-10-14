package com.github.rodexion.proper.scanner;


import static org.fest.assertions.api.Assertions.assertThat;

import org.fest.assertions.core.Condition;
import org.junit.Test;

/**
 * ScannerTest
 *
 * @author rodion
 * @version X.0
 * @since 2013/10/08
 */
public class ScannerTest {
  @Test
  public void scan() {
    ScanResult result = ProperScanners.scanner("com.github.rodexion.proper.scanner").scan();
    assertThat(result.getDeclarations()).hasSize(3)
            .haveExactly(1, declOf("test.prop.int", MyConstants.intPropLineNumber))
            .haveExactly(1, declOf("test.prop.lazyLong", MyConstants.lazyLongPropLineNumber))
            .haveExactly(1, declOf("test.float.prop", MyPropeties2.floatPropLineNumber));
  }

  private static Condition<ProperDecl> declOf(final String key, final int locatedAtLine) {
    return new Condition<ProperDecl>() {
      @Override
      public boolean matches(ProperDecl value) {
        return value.getLocation().getLineNumber() == locatedAtLine &&
                value.getProperty().getInfo().getKey().equals(key);
      }
    };
  }
}
