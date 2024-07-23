package ru.russeb.start;

import org.testng.TestNG;

import java.util.ArrayList;
import java.util.List;

public class StudentParserTestRunner {
    public static void main(String[] args) {
        TestNG testNG = new TestNG();
        List<String> suiteFiles = new ArrayList<>();
        suiteFiles.add(StudentParserTestRunner.class.getClassLoader().getResource("parser-testng.xml").getFile());
        testNG.setTestSuites(suiteFiles);
        testNG.run();
    }
}
