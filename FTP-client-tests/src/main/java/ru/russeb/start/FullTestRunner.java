package ru.russeb.start;

import org.testng.TestNG;

import java.util.ArrayList;
import java.util.List;

public class FullTestRunner {
    public static void main(String[] args) {
        TestNG testNG = new TestNG();
        List<String> suiteFiles = new ArrayList<>();
        suiteFiles.add("../FTP-client-tests/src/main/resources/full-testng.xml");
        testNG.setTestSuites(suiteFiles);
        testNG.run();
    }
}
