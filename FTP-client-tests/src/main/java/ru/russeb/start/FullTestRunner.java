package ru.russeb.start;

import org.testng.TestNG;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FullTestRunner {
    public static void main(String[] args) {
        TestNG testNG = new TestNG();
        List<String> suiteFiles = new ArrayList<>();
        File file = new File("src/main/resources/full-testng.xml");
        suiteFiles.add(file.getAbsolutePath());
        testNG.setTestSuites(suiteFiles);
        testNG.run();
    }
}
