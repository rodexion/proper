package com.github.rodexion.proper.scanner;

import static com.github.rodexion.proper.util.Preconditions.checkNotNull;

import com.github.rodexion.proper.annotations.ProperScannable;
import com.github.rodexion.proper.bus.InternalBuilderBus;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public ScanResult scan() {
      List<Exception> errors = new ArrayList<>();
      Set<Class<?>> classesToPreload = new HashSet<>();
      Reflections reflections = new Reflections(basePackage,
              new TypeAnnotationsScanner(),
              new FieldAnnotationsScanner());
      for (Class<?> clazz : reflections.getTypesAnnotatedWith(ProperScannable.class)) {
        classesToPreload.add(clazz);
      }
      for (Field field : reflections.getFieldsAnnotatedWith(ProperScannable.class)) {
        classesToPreload.add(field.getDeclaringClass());
      }
      ClassLoader scannerClassLoader = getClass().getClassLoader();
      for (Class<?> clazz : classesToPreload) {
        try {
          Class.forName(clazz.getName(), true, scannerClassLoader);
        } catch (ClassNotFoundException e) {
          errors.add(e);
        }
      }
      return new ScanResult(InternalBuilderBus.getFoundProperties(), errors);
    }
  }
}
