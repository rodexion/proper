package com.github.rodexion.proper.bus;

import lombok.Data;

/**
 * @author rodexion
 * @since 0.1
 */
@Data
public class ProperLocation {
  public static final ProperLocation UNKNOWN = new ProperLocation("UNKNOWN", -1);
  private final String fileName;
  private final int lineNumber;

  public static ProperLocation getCurrentDeclarationLocation() {
    for (StackTraceElement elem : new Exception().getStackTrace()) {
      System.out.println(elem);
      if (!frameWorkElem(elem)) {
        return new ProperLocation(elem.getFileName(), elem.getLineNumber());
      }
    }
    return UNKNOWN;
  }

  private static boolean frameWorkElem(StackTraceElement elem) {
    return !elem.getMethodName().equals("<clinit>");
//    String properRootPkgName = Proper.class.getPackage().getName();
//    return elem.getClassName().startsWith(properRootPkgName);
  }
}
