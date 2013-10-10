package com.github.rodexion.proper.scanner;

import com.github.rodexion.proper.Proper;

import java.util.List;

/**
 * @author rodexion
 * @since 0.1
 */
public interface ProperScanner {
  List<Proper.Ty<?>> scan();
}
