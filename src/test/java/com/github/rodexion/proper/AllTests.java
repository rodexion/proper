/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.github.rodexion.proper;

import static org.junit.runners.Suite.SuiteClasses;

import com.github.rodexion.proper.scanner.AllScannerTests;
import com.github.rodexion.proper.validator.AllValidatorTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author rodexion
 * @since 0.1
 */
@RunWith(Suite.class)
@SuiteClasses({
        AllScannerTests.class,
        AllValidatorTests.class,
        ConverterProviderBuilderTest.class,
        DefaultConvertersTest.class,
        Examples.class})
public class AllTests {
}
