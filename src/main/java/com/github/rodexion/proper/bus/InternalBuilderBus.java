package com.github.rodexion.proper.bus;

import com.github.rodexion.proper.Proper;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rodexion
 * @since 0.1
 */
public class InternalBuilderBus {
  private static final Map<Proper.Ty<?>, ProperLocation> properties = new HashMap<>();

  public static void firePropertyBuilt(Proper.Ty<?> info, ProperLocation location) {
    System.out.println("Fired" + InternalBuilderBus.class.getClassLoader());
    properties.put(info, location);
  }

  public static Map<Proper.Ty<?>, ProperLocation> getFoundProperties() {
    return new HashMap<>(properties);
  }
}
