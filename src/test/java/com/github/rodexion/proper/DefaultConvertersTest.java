package com.github.rodexion.proper;


import static org.fest.assertions.api.Assertions.assertThat;

import lombok.Data;
import org.junit.Test;

import java.util.Collections;

/**
 * @author rodexion
 * @since 0.1
 */
public class DefaultConvertersTest {
  enum TestEnum {
    One, Two, Three;
  }

  @Test
  public void byteConversion() {
    checkOk(Byte.class, byte.class,
            ok("123", (byte) 123),
            ok("-34", (byte) -34),
            ok(" 45 ", (byte) 45));

    String typeName = "byte";
    checkFailure(Byte.class, byte.class,
            fail("", msg("", typeName)),
            fail("456.0", msg("456.0", typeName)),
            fail("abc", msg("abc", typeName)));
  }

  private String msg(String value, String typeName) {
    return "Could not convert property: key=\"key\" value=\"" + value + "\" type=\"" + typeName;
  }

  @Test
  public void shortConversion() {
    checkOk(Short.class, short.class,
            ok("123", (short) 123),
            ok("-234", (short) -234),
            ok(" 345 ", (short) 345));

    String typeName = "short";
    checkFailure(Short.class, short.class,
            fail("", msg("", typeName)),
            fail("456.0", msg("456.0", typeName)),
            fail("abc", msg("abc", typeName)));
  }

  @Test
  public void intConversion() {
    checkOk(Integer.class, int.class,
            ok("123", 123),
            ok("-234", -234),
            ok(" 345 ", 345));

    String typeName = "int";
    checkFailure(Integer.class, int.class,
            fail("", msg("", typeName)),
            fail("456.0", msg("456.0", typeName)),
            fail("abc", msg("abc", typeName)));
  }

  @Test
  public void longConversion() {
    checkOk(Long.class, long.class,
            ok("123", 123L),
            ok("-234", -234L),
            ok(" 345 ", 345L));

    String typeName = "long";
    checkFailure(Long.class, long.class,
            fail("", msg("", typeName)),
            fail("456.0", msg("456.0", typeName)),
            fail("abc", msg("abc", typeName)));
  }

  @Test
  public void floatConversion() {
    checkOk(Float.class, float.class,
            ok("123", 123f),
            ok("-234", -234f),
            ok(" 345 ", 345f),
            ok("234.345", 234.345f),
            ok(" -456.11", -456.11f));

    String typeName = "float";
    checkFailure(Float.class, float.class,
            fail("", msg("", typeName)),
            fail("abc", msg("abc", typeName)));
  }

  @Test
  public void doubleConversion() {
    checkOk(Double.class, double.class,
            ok("123", 123d),
            ok("-234", -234d),
            ok(" 345 ", 345d),
            ok("234.345", 234.345d),
            ok(" -456.11", -456.11d));

    String typeName = "double";
    checkFailure(Double.class, double.class,
            fail("", msg("", typeName)),
            fail("abc", msg("abc", typeName)));
  }

  @Test
  public void characterConversion() {
    checkOk(Character.class, char.class,
            ok("a", 'a'),
            ok(" b ", 'b'),
            ok(" ", ' '));

    checkFailure(Character.class, char.class,
            fail("", "has to be exactly 1 character"),
            fail("abc", "has to be exactly 1 character"));
  }

  @Test
  public void enumConversion() {
    checkOk(TestEnum.class,
            ok("One", TestEnum.One),
            ok("two", TestEnum.Two),
            ok(" THREE ", TestEnum.Three));

    checkFailure(TestEnum.class,
            fail("Four", msg("Four", TestEnum.class.getCanonicalName())));
  }

  @SafeVarargs
  private final <T> void checkOk(Class<T> typeClass, Class<T> typeClass2,
                                 InputToOk<T>... inputs) {
    checkOk(typeClass, inputs);
    checkOk(typeClass2, inputs);
  }

  @SafeVarargs
  private final <T> void checkOk(Class<T> typeClass,
                                 InputToOk<T>... inputs) {
    for (InputToOk<T> input : inputs) {
      assertThat(conv(typeClass).convert(input.getInput(), property(typeClass)))
              .isEqualTo(Converter.Result.ok(input.getExpected()));
    }
  }

  private <T> void checkFailure(Class<T> typeClass, Class<T> typeClass2,
                                InputToFailure... inputs) {
    checkFailure(typeClass, inputs);
    checkFailure(typeClass2, inputs);
  }

  private <T> void checkFailure(Class<T> typeClass,
                                InputToFailure... inputs) {
    for (InputToFailure input : inputs) {
      assertThat(conv(typeClass).convert(input.getInput(), property(typeClass)))
              .is(Conditions.<T>convertFailure(input.getErrorContains()));
    }
  }

  private <T> InputToOk<T> ok(String input, T expected) {
    return new InputToOk<>(input, expected);
  }

  private InputToFailure fail(String input, String errorContains) {
    return new InputToFailure(input, errorContains);
  }

  private <T> Converter<T> conv(Class<T> typeClass) {
    return Converters.defaultConverterProvider().getConverter(typeClass);
  }

  private static <T> Proper.Ty<T> property(Class<T> type) {
    return new Proper.Ty<>("key", type, null, Collections.<String, Object>emptyMap());
  }

  @Data
  private static final class InputToOk<T> {
    private final String input;
    private final T expected;
  }

  @Data
  private static final class InputToFailure {
    private final String input;
    private final String errorContains;
  }
}
