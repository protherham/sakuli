/*
 * Copyright (c) 2015 - M-net Telekommunikations GmbH
 * All rights reserved.
 * -------------------------------------------------------
 * File created: 02.02.2015
 */
package de.consol.sakuli;

import de.consol.sakuli.datamodel.actions.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Scanner;

import static org.testng.Assert.assertEquals;

/**
 * @author Tobias Schneck
 */
public abstract class AbstractLogAwareTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    public static String getResource(String resourceName) {
        try {
            return Paths.get(BaseTest.class.getResource(resourceName).toURI()).toString();
        } catch (URISyntaxException e) {
            LOGGER.error("could not resolve Testsuite from classpath resource '{}'", resourceName, e);
            return null;
        }
    }

    public static void deleteFile(Path logFile) {
        FileSystemProvider provider = logFile.getFileSystem().provider();
        try {
            provider.deleteIfExists(logFile);
        } catch (IOException e) {
            //do nothing
        }
    }

    public static String getLastLineWithContent(Path file, String s) throws IOException {

        Scanner in;
        String lastLine = "";

        in = new Scanner(Files.newInputStream(file));
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.contains(s)) {
                lastLine = line;
            }
        }
        return lastLine;

    }

    public static String getLastLineOfLogFile(Path file) throws IOException {
        return getLastLineWithContent(file, "");
    }

    public static String getLastLineOfLogFile(Path file, int lastLines) throws IOException {
        Scanner in;
        StringBuilder result = new StringBuilder();

        in = new Scanner(Files.newInputStream(file));
        int countOfLines = 0;
        while (in.hasNextLine()) {
            countOfLines++;
            in.nextLine();
        }

        in = new Scanner(Files.newInputStream(file));
        int countOfReadInLines = 0;
        while (in.hasNextLine()) {
            countOfReadInLines++;
            String line = in.nextLine();
            if (countOfLines - countOfReadInLines <= lastLines) {
                result.append(line).append("\n");
            }
        }


        return result.toString();
    }

    public static void setSystemProperty(String value, String key) {
        if (value != null) {
            System.setProperty(key, value);
        } else {
            System.clearProperty(key);
        }
    }

    /**
     * Set the property 'log-level-sakuli' for the file 'sakuli-log-config.xml'.
     *
     * @param logLevel as String e.g. 'DEBUG'
     */
    public static void setSakuliLogLevel(String logLevel) {
        setSystemProperty(logLevel, "log-level-sakuli");
    }

    /**
     * Set the property 'log-level-sikuli' for the file 'sakuli-log-config.xml'.
     *
     * @param logLevel as String e.g. 'DEBUG'
     */
    public static void setSikuliLogLevel(String logLevel) {
        setSystemProperty(logLevel, "log-level-sikuli");
    }

    @BeforeSuite(alwaysRun = true)
    public void setLogLevel() throws Exception {
        AbstractLogAwareTest.setSakuliLogLevel("DEBUG");
        AbstractLogAwareTest.setSikuliLogLevel("DEBUG");
    }

    @AfterSuite(alwaysRun = true)
    public void removeLogLevel() throws Exception {
        AbstractLogAwareTest.setSakuliLogLevel(null);
        AbstractLogAwareTest.setSikuliLogLevel(null);
    }

    protected void assertLastLine(Path logFile, String filter, LogLevel logLevel, String expectedMessage) throws IOException {
        String preFix = null;
        switch (logLevel) {
            case ERROR:
                preFix = "ERROR";
                break;
            case INFO:
                preFix = "INFO ";
                break;
            case DEBUG:
                preFix = "DEBUG";
                break;
            case WARNING:
                preFix = "WARN ";
                break;
        }
        String lastLineOfLogFile = AbstractLogAwareTest.getLastLineWithContent(logFile, filter);
        assertEquals(lastLineOfLogFile.substring(0, 5), preFix);
        assertEquals(lastLineOfLogFile.substring(lastLineOfLogFile.indexOf("]") + 4), expectedMessage);
    }
}