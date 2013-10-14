package com.github.rodexion.proper.scanner;

import com.github.rodexion.proper.Proper;
import com.github.rodexion.proper.bus.ProperLocation;
import lombok.Data;

/**
 * @author rodexion
 * @since 0.1
 */
@Data
public class ProperDecl {
  private final Proper.Ty<?> property;
  private final ProperLocation location;
}
