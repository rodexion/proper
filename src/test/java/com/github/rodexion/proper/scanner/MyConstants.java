package com.github.rodexion.proper.scanner;

import com.github.rodexion.proper.Examples;
import com.github.rodexion.proper.LazyValue;
import com.github.rodexion.proper.annotations.ProperScannable;

/**
 * MyConstants
 *
 * @author rodion
 * @version X.0
 * @since 2013/10/08
 */
@ProperScannable
public class MyConstants {
    public static final int intProp = Examples.MyProper.ty("test.prop.int", 123,
            "Test integer property", Examples.MyProper.Unit.Int).build().getValue();
    public static final LazyValue<Long> lazyLongProp = Examples.MyProper.ty("test.prop.lazyLong", 234L,
            "Test lazy long propery", Examples.MyProper.Unit.Long).build();
}
