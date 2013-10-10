package com.github.rodexion.proper;

import static org.junit.runners.Suite.SuiteClasses;

import com.github.rodexion.proper.scanner.AllScannerTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author rodexion
 * @since 0.1
 */
@RunWith(Suite.class)
@SuiteClasses({
        AllScannerTests.class,
        ConverterProviderBuilderTest.class,
        DefaultConvertersTest.class,
        Examples.class})
public class AllTests {
}
