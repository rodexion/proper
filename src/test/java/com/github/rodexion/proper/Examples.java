package com.github.rodexion.proper;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rodexion
 * @since 0.1
 */
public class Examples {
  @Rule
  public TestRule tmpProps = RuleUtils.tmpSysProp("my.key");

  @SuppressWarnings("UnusedDeclaration")
  enum MyEnum {
    One, Two, Three;
  }

  /**
   * Your are recommended to define your own static property accessor.
   * <p/>
   * In the accessor you can set up your own default converters, validators
   * and property listeners.
   */
  static class MyProper {
    static final List<String> messages = new ArrayList<>();

    public static <T> PropertyBuilder<T> ty(String key, T defaultValue) {
      return Proper.tyBuilder(key, defaultValue)
              //Register a custom property listener
              .propertyListener(new PropertyListeners.BasePropertyListener() {
                @Override
                public void notFound(Proper.Info<?> info) {
                  messages.add("Not found " + info.getKey() + " using default " + info.getDefaultValue());
                }

                @Override
                public void conversionFailed(String value, String conversionError, Proper.Info<?> info) {
                  messages.add("Could not convert " + value + " to int");
                }

                @Override
                public void validationAfterConversionFailed(Object value, String validationError, Proper.Info<?> info) {
                  messages.add(validationError);
                }
              })
                      //Setup custom converters
              .converterProvider(ConverterProviders.builder()
                      //Add converters for your custom types (btw, enums are converted by default!)
                      .add(new Converters.BaseConverter<MyEnum>(MyEnum.class) {
                        @Override
                        protected MyEnum doConvert(String value, Proper.Info<MyEnum> info) {
                          return MyEnum.valueOf(value);
                        }
                      })
                              //Add all the default converters if (primitive types, BigDecimal, Enums, etc.)
                      .allFrom(ConverterProviders.defaultConverterProvider())
                      .build())
                      //Setup custom common validation
              .validatorProvider(ValidatorProviders.builder()
                      .add(new Validators.BaseValidator<MyEnum>(MyEnum.class) {
                        @Override
                        public Result afterConversion(MyEnum value, Proper.Info<MyEnum> info) {
                          return value == MyEnum.One ? ok() : fail("value has to be 'One'");
                        }
                      }).build()
              );
    }
  }

  @Test
  public void basicUsage() {
    //Consider "my.key" not set
    Proper.Ty<Integer> intProp = MyProper.ty("my.key", 123).build();
    assertThat(intProp.getValue()).isEqualTo(123);
    assertThat(MyProper.messages.contains("Not found my.key using default 123"));
    MyProper.messages.clear();

    //Set it to a correct value
    System.setProperty("my.key", "234");
    assertThat(intProp.getValue()).isEqualTo(234);
    assertThat(MyProper.messages).isEmpty();

    //Set it to a wrong value
    System.setProperty("my.key", "xyz");
    assertThat(intProp.getValue()).isEqualTo(123);//default
    assertThat(MyProper.messages.contains("Could not convert xyz to int"));
    MyProper.messages.clear();
  }

  @Test
  public void validationExample() {
    Proper.Ty<Long> longProp = MyProper.ty("my.key", 123L)
            .validator(Validators.<Long>comparableValidatorBuilder()
                    .min(0L)
                    .max(1000L)
                    .build())
            .build();

    //Try a valid value
    System.setProperty("my.key", "999");
    assertThat(longProp.getValue()).isEqualTo(999L);
    assertThat(MyProper.messages).isEmpty();

    //Try value out of valid scope
    System.setProperty("my.key", "1001");
    assertThat(longProp.getValue()).isEqualTo(123L);//default
    assertThat(MyProper.messages).contains("1001 is greater than 1000");
    MyProper.messages.clear();
  }
}
