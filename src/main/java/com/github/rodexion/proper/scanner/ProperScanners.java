package com.github.rodexion.proper.scanner;

import static com.github.rodexion.proper.util.Preconditions.checkNotNull;

import com.github.rodexion.proper.annotations.ProperScannable;
import com.github.rodexion.proper.bus.InternalBuilderBus;
import lombok.AllArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

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
    return new ProperScannableScanner(checkNotNull("basePackage", basePackage));
  }

  public static ProperScanner scanAll(String basePackage) {
    return new AllClassesScanner(checkNotNull("basePackage", basePackage));
  }

  @AllArgsConstructor
  private static abstract class ReflectionsScanner implements ProperScanner {
    final String basePackage;

    @Override
    public ScanResult scan() {
      List<Exception> errors = new ArrayList<>();
      Reflections reflections = createReflections();
      ClassLoader scannerClassLoader = ProperScanners.class.getClassLoader();
      for (Class<?> clazz : gatherClasses(reflections)) {
        try {
          Class.forName(clazz.getName(), true, scannerClassLoader);
        } catch (ClassNotFoundException e) {
          errors.add(e);
        }
      }
      return new ScanResult(InternalBuilderBus.getFoundProperties(), errors);
    }

    abstract Reflections createReflections();

    abstract Set<Class<?>> gatherClasses(Reflections reflections);
  }

  private static final class ProperScannableScanner extends ReflectionsScanner {
    ProperScannableScanner(String basePackage) {
      super(basePackage);
    }

    @Override
    Reflections createReflections() {
      return new Reflections(
              new ConfigurationBuilder()
                      .filterInputsBy(new FilterBuilder().includePackage(basePackage))
                      .addScanners(
                              new TypeAnnotationsScanner(),
                              new FieldAnnotationsScanner())
                      .setUrls(ClasspathHelper.forPackage(basePackage)));
    }

    @Override
    Set<Class<?>> gatherClasses(Reflections reflections) {
      Set<Class<?>> classesToPreload = new HashSet<>();
      for (Class<?> clazz : reflections.getTypesAnnotatedWith(ProperScannable.class)) {
        classesToPreload.add(clazz);
      }
      for (Field field : reflections.getFieldsAnnotatedWith(ProperScannable.class)) {
        classesToPreload.add(field.getDeclaringClass());
      }
      return classesToPreload;
    }
  }

  private static final class AllClassesScanner extends ReflectionsScanner {
    AllClassesScanner(String basePackage) {
      super(basePackage);
    }

    @Override
    Reflections createReflections() {
      return new Reflections(
              new ConfigurationBuilder()
                      .filterInputsBy(new FilterBuilder().includePackage(basePackage))
                      .setUrls(ClasspathHelper.forPackage(basePackage))
                      .setScanners(
                              new SubTypesScanner(/*Do not exclude Object subtypes*/ false),
                              new ResourcesScanner()));
    }

    @Override
    Set<Class<?>> gatherClasses(Reflections reflections) {
      return reflections.getSubTypesOf(Object.class);
    }
  }
}
