package com.github.rodexion.proper.bus;

import javassist.NotFoundException;
import org.junit.Test;

/**
 * @author rodexion
 * @since 0.1
 */
public class ProperLocationTest {
  private static final ProperLocation pl = ProperLocation.getCurrentDeclarationLocation();
  private static final ProperLocation pl2 = ProperLocation.getCurrentDeclarationLocation();

  @Test
  public void test() throws NotFoundException {

  }
}
