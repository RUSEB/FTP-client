package ru.russeb.start;

import org.testng.TestNG;

import java.util.ArrayList;
import java.util.List;

public class StudentParserTestRunner {
    public static void main(String[] args) {
        TestNG testNG = new TestNG();
        List<String> suiteFiles = new ArrayList<>();
        suiteFiles.add("../src/main/resources/parser-testng.xml");
        testNG.setTestSuites(suiteFiles);
        testNG.run();
    }
}
