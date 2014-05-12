/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.consol.sakuli.starter;

import de.consol.sakuli.BaseTest;
import de.consol.sakuli.starter.proxy.SahiProxy;
import de.consol.sakuli.utils.SakuliProperties;
import de.consol.sakuli.utils.SakuliPropertyPlaceholderConfigurer;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SakuliStarterTest extends BaseTest {

    @AfterMethod
    public void tearDown() throws Exception {
        //ensure that the system wide static variables point to the right values
        SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE = TEST_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.INCLUDE_FOLDER_VALUE = INCLUDE_FOLDER_PATH;
        SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE = SAHI_FOLDER_PATH;
    }

    @Test
    public void testCheckSahiProxyHomeAndSetContextVariables() throws Exception {
        //test with test suite folder to ensure that file is present
        final String absoluteSahiPath = Paths.get(TEST_FOLDER_PATH).toAbsolutePath().toString();
        String log = "";
        log = SakuliStarter.checkSahiProxyHomeAndSetContextVariables(TEST_FOLDER_PATH, log);
        assertEquals(absoluteSahiPath, SakuliPropertyPlaceholderConfigurer.SAHI_PROXY_HOME_VALUE);
        assertEquals(log, "\nset property '" + SahiProxy.SAHI_PROXY_HOME + "' to \"" + absoluteSahiPath + "\"");
    }

    @Test(expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = "sahi folder .* does not exist!")
    public void testCheckSahiProxyHomeAndSetContextVariablesFolderMissing() throws Exception {
        SakuliStarter.checkSahiProxyHomeAndSetContextVariables("not-valid-path/to-sahi-home", "");
    }

    @Test
    public void testCheckTestSuiteFolderAndSetContextVariables() throws Exception {
        final String absoluteTestSuitePath = Paths.get(TEST_FOLDER_PATH).toAbsolutePath().toString();
        String log = "";
        log = SakuliStarter.checkTestSuiteFolderAndSetContextVariables(TEST_FOLDER_PATH, log);
        assertEquals(absoluteTestSuitePath, SakuliPropertyPlaceholderConfigurer.TEST_SUITE_FOLDER_VALUE);
        assertEquals(log, "\nset property '" + SakuliProperties.TEST_SUITE_FOLDER + "' to \"" + absoluteTestSuitePath + "\"");
    }

    @Test(expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = "sakuli test suite folder .* does not exist!")
    public void testCheckTestSuiteFolderAndSetContextVariablesFolderMissing() throws Exception {
        SakuliStarter.checkTestSuiteFolderAndSetContextVariables("not-valid-path/to-suite", "");
    }

    @Test(expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = "property file \"testsuite.properties\" does not exist in folder.*")
    public void testCheckTestSuiteFolderAndSetContextVariablesSuitePropertyMissing() throws Exception {
        Path path = Paths.get(getClass().getResource("unvalid").toURI());
        assertTrue(Files.exists(path));
        SakuliStarter.checkTestSuiteFolderAndSetContextVariables(path.toString(), "");
    }

    @Test(expectedExceptions = FileNotFoundException.class, expectedExceptionsMessageRegExp = "suite file \"testsuite.suite\" does not exist in folder.*")
    public void testCheckTestSuiteFolderAndSetContextVariablesSuiteFileMissing() throws Exception {
        Path path = Paths.get(getClass().getResource("unvalid2").toURI());
        assertTrue(Files.exists(path));
        SakuliStarter.checkTestSuiteFolderAndSetContextVariables(path.toString(), "");
    }
}