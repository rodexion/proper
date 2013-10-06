package com.github.rodexion.proper;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

/**
 * @author rodexion
 * @since 0.1
 */
public class ProperScenarioTest {
  @Rule
  public TestRule tmpProps = RuleUtils.tmpSysProp("my.key");

  static class MyProper {
    public static <T> PropertyBuilder<T> ty(String key, T defaultValue) {
      return Proper.ty(key, defaultValue);
    }
  }

  @Test
  public void stringProperty() {
    PropertyBuilder<String> pb = Proper.ty("my.key", "default");
    assertThat(pb.value()).isEqualTo("default");
    System.setProperty("my.key", "other value");
    assertThat(pb.value()).isEqualTo("other value");
  }

  @Test
  public void intProperty() {
    PropertyBuilder<Integer> pb = Proper.ty("my.key", 1)
            .converter(new Converters.BaseConverter<Integer>(Integer.class) {
              @Override
              public Integer doConvert(String value, Proper.Ty<Integer> info) {
                return Integer.parseInt(value);
              }
            });
    assertThat(pb.value()).isEqualTo(1);
    System.setProperty("my.key", "2");
    assertThat(pb.value()).isEqualTo(2);
  }

  @Test
  public void validatedInt() {
    PropertyBuilder<Integer> pb = Proper.ty("my.key", 1)
            .converter(new Converters.BaseConverter<Integer>(Integer.class) {
              @Override
              public Integer doConvert(String value, Proper.Ty<Integer> info) {
                return Integer.parseInt(value);
              }
            })
            .validator(new Validators.BaseValidator<Integer>() {
              @Override
              public Result beforeConversion(String value) {
                return value.endsWith("3") ? ok() : fail("Value does not end with 3");
              }

              @Override
              public Result afterConversion(Integer value) {
                return value == 123 ? ok() : fail("Int value is not 123");
              }
            });
    assertThat(pb.value()).isEqualTo(1);
    System.setProperty("my.key", "234");
    assertThat(pb.value()).isEqualTo(1);
    System.setProperty("my.key", "23");
    assertThat(pb.value()).isEqualTo(1);
    System.setProperty("my.key", "123");
    assertThat(pb.value()).isEqualTo(123);
  }

  @Test
  public void aoeu() {

  }
}
