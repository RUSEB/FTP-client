package ru.russeb.start;

import org.testng.TestNG;

import java.util.ArrayList;
import java.util.List;

public class FtpClientTestRunner {
    public static void main(String[] args) {
        TestNG testNG = new TestNG();
        List<String> suiteFiles = new ArrayList<>();
        suiteFiles.add("../src/main/resources/ftp-testng.xml");
        testNG.setTestSuites(suiteFiles);
        testNG.run();
    }
}
