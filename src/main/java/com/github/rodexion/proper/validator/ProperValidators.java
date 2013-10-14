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

package com.github.rodexion.proper.validator;

import static com.github.rodexion.proper.util.Preconditions.checkNotNull;

import com.github.rodexion.proper.Proper;
import com.github.rodexion.proper.PropertyListeners;
import com.github.rodexion.proper.annotations.ProperScannable;
import com.github.rodexion.proper.scanner.ProperDecl;
import com.github.rodexion.proper.scanner.ProperScanner;
import com.github.rodexion.proper.scanner.ProperScanners;
import com.github.rodexion.proper.util.Opt;
import lombok.AllArgsConstructor;

import java.util.*;

/**
 * @author rodexion
 * @since 0.1
 */
public class ProperValidators {
  public static ProperValidator validator(String basePackage) {
    ProperScanner scanner = ProperScanners.scanner(checkNotNull("basePackage", basePackage));
    return new DefaultProperValidator(scanner);
  }

  public static ProperValidator validateAll(String basePackage) {
    checkNotNull("basePackage", basePackage);
    ProperScanner annotationScanner = ProperScanners.scanner(basePackage);
    ProperScanner allScanner = ProperScanners.scanAll(basePackage);
    ProperValidator allClassValidator = new DefaultProperValidator(allScanner);
    ProperValidator missingAnnotationValidator = new MissingScannableAnnotationsValidator(allScanner, annotationScanner);
    return new CombinedValidator(missingAnnotationValidator, allClassValidator);
  }

  @AllArgsConstructor
  private static final class DefaultProperValidator implements ProperValidator {
    private final ProperScanner scanner;

    @Override
    public ValidationResult validate() {
      List<ValidationResult.Error> errors = new ArrayList<>();
      for (ProperDecl properDecl : scanner.scan().getDeclarations()) {
        ValidationErrorCapture errorCapture = new ValidationErrorCapture();
        properDecl.getProperty().getValue(errorCapture);
        if (errorCapture.hasError) {
          errors.add(new ValidationResult.Error(
                  properDecl.getProperty(),
                  properDecl.getLocation(),
                  errorCapture.validationError.get()));
        }
      }
      return new ValidationResult(errors);
    }

    private static final class ValidationErrorCapture extends PropertyListeners.BasePropertyListener {
      Opt<String> inputValue = Opt.none();
      Opt<String> validationError = Opt.none();
      boolean hasError = false;

      @Override
      public void validationBeforeConversionFailed(String value, String validationError, Proper.Info<?> info) {
        validationError(value, validationError);
      }

      @Override
      public void conversionFailed(String value, String conversionError, Proper.Info<?> info) {
        validationError(value, conversionError);
      }

      @Override
      public void validationAfterConversionFailed(Object value, String validationError, Proper.Info<?> info) {
        validationError(value, validationError);
      }

      private void validationError(Object value, String validationError) {
        hasError = true;
        inputValue = Opt.some(String.valueOf(value));
        this.validationError = Opt.some(validationError);
      }
    }
  }

  @AllArgsConstructor
  private static final class MissingScannableAnnotationsValidator implements ProperValidator {
    private final ProperScanner scanAll;
    private final ProperScanner scanAnnotated;

    @Override
    public ValidationResult validate() {
      List<ValidationResult.Error> errors = new ArrayList<>();
      Set<ProperDecl> annotatedDecls = new HashSet<>(scanAnnotated.scan().getDeclarations());
      Set<ProperDecl> allDecls = new HashSet<>(scanAll.scan().getDeclarations());
      allDecls.removeAll(annotatedDecls);
      for (ProperDecl notAnnotatedDecl : allDecls) {
        errors.add(new ValidationResult.Error(
                notAnnotatedDecl.getProperty(),
                notAnnotatedDecl.getLocation(),
                String.format("Property '%s' declared in %s@%d is missing @%s annotation.",
                        notAnnotatedDecl.getProperty().getInfo().getKey(),
                        notAnnotatedDecl.getLocation().getFileName(),
                        notAnnotatedDecl.getLocation().getLineNumber(),
                        ProperScannable.class.getSimpleName())));
      }
      return new ValidationResult(errors);
    }
  }

  @AllArgsConstructor
  private static final class CombinedValidator implements ProperValidator {
    private final List<ProperValidator> validators;

    CombinedValidator(ProperValidator... validators) {
      this(Arrays.asList(validators));
    }

    @Override
    public ValidationResult validate() {
      List<ValidationResult.Error> mergedResult = new ArrayList<>();
      for (ProperValidator validator : validators) {
        mergedResult.addAll(validator.validate().getValidationErrors());
      }
      return new ValidationResult(mergedResult);
    }
  }
}
