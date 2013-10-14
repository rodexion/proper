package com.github.rodexion.proper.bus;

import com.github.rodexion.proper.Proper;
import com.github.rodexion.proper.scanner.ProperDecl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rodexion
 * @since 0.1
 */
public class InternalBuilderBus {
  private static final List<ProperDecl> properties = new ArrayList<>();

  public static void firePropertyBuilt(Proper.Ty<?> info, ProperLocation location) {
    properties.add(new ProperDecl(info, location));
  }

  public static List<ProperDecl> getFoundProperties() {
    return new ArrayList<>(properties);
  }
}
