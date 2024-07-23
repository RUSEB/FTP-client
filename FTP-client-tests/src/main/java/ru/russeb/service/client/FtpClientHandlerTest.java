package ru.russeb.service.client;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class FtpClientHandlerTest {

    private static final Logger log = Logger.getLogger(FtpClientHandlerTest.class.getName());

    private QueryHandler queryHandler;

    @Test
    @Parameters({"host","port","typeOfConnection"})
    public void testConnection(String host, int port,TypeOfConnection typeOfConnection) throws IOException {
        log.info("Connecting to " + host + ":" + port);
        queryHandler = new QueryHandler(host, port);
        queryHandler.chooseTypeOfConnection(typeOfConnection);
        log.info("Connection to " + host + ":" + port+ " was established.\n");
    }

    @Test(dependsOnMethods = "testConnection")
    @Parameters({"login","password"})
    public void testAuth(String login, String password) throws IOException, TimeoutException {
        log.info("Auth with login: " + login + "and password: " + password);
        queryHandler.login(login,password);
        log.info("Auth with login: " + login + " and password: " + password + " was successful.\n");
    }

    @Test(dependsOnMethods = "testAuth")
    @Parameters({"pathToFile"})
    public void testReadFile(String pathToFile) throws IOException, TimeoutException {
        log.info("Reading file: " + pathToFile);
        String str = queryHandler.readFile(pathToFile);
        log.info("Text in file: "+str);
    }

    @Parameters({"pathToFile"})
    @Test(dependsOnMethods = "testReadFile")
    public void testWriteFile(String pathToFile) throws IOException, TimeoutException {
        log.info("Writing to file: " + pathToFile);
        String json = "{\"students\":[]}";
        queryHandler.writeFile(pathToFile,json);
        log.info("Wrote to file: "+pathToFile +"\ntext: "+json);
    }
}
