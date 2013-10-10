package com.github.rodexion.proper.scanner;

import static com.github.rodexion.proper.util.Preconditions.checkNotNull;

import com.github.rodexion.proper.Proper;
import com.github.rodexion.proper.annotations.ProperScannable;
import com.github.rodexion.proper.bus.InternalBuilderBus;
import com.github.rodexion.proper.bus.ProperLocation;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rodexion
 * @since 0.1
 */
public class ProperScanners {
  public static ProperScanner scanner(String basePackage) {
    return new ReflectionsScanner(checkNotNull("basePackage", basePackage));
  }

  private static final class ReflectionsScanner implements ProperScanner {
    private final String basePackage;

    private ReflectionsScanner(String basePackage) {
      this.basePackage = basePackage;
    }

    @Override
    public List<Proper.Ty<?>> scan() {
      for (Class<?> aClass : new Reflections(basePackage).getTypesAnnotatedWith(ProperScannable.class)) {
        try {
          Class.forName(aClass.getName(), true, aClass.getClassLoader());
        } catch (ClassNotFoundException e) {
          //TODO
        }
        System.out.println(aClass.getCanonicalName());
        System.out.println(aClass.getClassLoader().toString());
      }
      for (ProperLocation properLocation : InternalBuilderBus.getFoundProperties().values()) {
        System.out.println(properLocation);
      }

      return new ArrayList<>(InternalBuilderBus.getFoundProperties().keySet());
    }
  }
}
