package com.github.rodexion.proper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.ArrayList;
import java.util.List;

import static com.github.rodexion.proper.DefaultConvertersTest.TestEnum;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author rodexion
 * @since 0.1
 */
public class ProperScenarioTest {
    @Rule
    public TestRule tmpProps = RuleUtils.tmpSysProp("my.key");

    /**
     * Your are recommended to define your own static property accessor.
     * <p/>
     * In the accessor you can set up your own default converters, validators
     * and property listeners.
     */
    static class MyProper {
        static final List<String> messages = new ArrayList<>();

        public static <T> PropertyBuilder<T> ty(String key, T defaultValue) {
            return Proper.ty(key, defaultValue)
                    //Register a property listener
                    .propertyListener(new PropertyListeners.BasePropertyListener() {
                        @Override
                        public void notFound(Proper.Ty<?> info) {
                            messages.add("Not found " + info.getKey() + " using default " + info.getDefaultValue());
                        }
                    })
                            //Setup custom converters
                    .converterProvider(ConverterProviderBuilder.builder()
                            //Add converters for your custom types
                            .converter(TestEnum.class, new Converter<TestEnum>() {
                                @Override
                                public boolean canConvert(Class<?> type) {
                                    return type.equals(TestEnum.class);
                                }

                                @Override
                                public Result<TestEnum> convert(String value, Proper.Ty<TestEnum> info) {
                                    return Result.ok(TestEnum.valueOf(value));
                                }
                            })
                                    //Add default converters (primitive types, BigDecimal, Enums, etc.)
                            .convertersFrom(Converters.defaultConverterProvider())
                            .build());
        }
    }

    @Test
    public void test() {
        PropertyBuilder<Integer> intProp = MyProper.ty("my.key", 123);
        assertThat(intProp.value()).isEqualTo(123);
        assertThat(MyProper.messages.contains("Not found my.key using default 123"));
        MyProper.messages.clear();
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
