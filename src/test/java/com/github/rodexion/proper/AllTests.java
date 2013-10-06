package com.github.rodexion.proper;

import static org.junit.runners.Suite.SuiteClasses;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author rodexion
 * @since 0.1
 */
@RunWith(Suite.class)
@SuiteClasses({
        ConverterProviderBuilderTest.class,
        DefaultConvertersTest.class,
        ProperScenarioTest.class})
public class AllTests {
}
