package com.github.rodexion.proper.scanner;

import lombok.Data;

import java.util.List;

/**
 * @author rodexion
 * @since 0.1
 */
@Data
public class ScanResult {
  private final List<ProperDecl> declarations;
  private final List<Exception> errors;
}
