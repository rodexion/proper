package com.github.rodexion.proper.scanner;


import static org.fest.assertions.api.Assertions.assertThat;

import com.github.rodexion.proper.Proper;
import org.fest.assertions.core.Condition;
import org.junit.Test;

import java.util.List;

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
    List<Proper.Ty<?>> result = ProperScanners.scanner("com.github.rodexion").scan();
    assertThat(result).hasSize(2)
            .haveExactly(1, new Condition<Proper.Ty<?>>() {
              @Override
              public boolean matches(Proper.Ty<?> value) {
                return value.getInfo().getKey().equals("test.prop.int");
              }
            })
            .haveExactly(1, new Condition<Proper.Ty<?>>() {
              @Override
              public boolean matches(Proper.Ty<?> value) {
                return value.getInfo().getKey().equals("test.prop.lazyLong");
              }
            });
  }
}
