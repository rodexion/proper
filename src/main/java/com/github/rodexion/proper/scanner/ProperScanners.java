/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

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
 * <p>A collection of default scanner implementations.</p>
 *
 * @author rodexion
 * @since 0.1
 */
public class ProperScanners {
  /**
   * <p>Create a scanner for all property declarations under the specified package,
   * including sub-packages.</p>
   * <p>Scanner only searches for classes annotated with {@link ProperScannable}.</p>
   *
   * @param basePackage Package to scan under (not-null)
   * @return Property scanner object (not-null)
   */
  public static ProperScanner scanner(String basePackage) {
    return new ProperScannableScanner(checkNotNull("basePackage", basePackage));
  }

  /**
   * <p>Create a scanner for all property declarations under the specified package,
   * including sub-packages.</p>
   * <p>Scanner only searches for classes in the whole classpath, in contrast to {@link #scanner(String)}.
   * This scanner is therefore uses more resources, and should be used sparingly.</p>
   *
   * @param basePackage Package to scan under (not-null)
   * @return Property scanner object (not-null)
   */
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
