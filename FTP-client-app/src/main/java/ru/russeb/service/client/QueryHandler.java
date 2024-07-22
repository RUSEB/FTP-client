package ru.russeb.service.client;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryHandler  implements AutoCloseable {
    private static final String IP_SERVER_PATTERN = "\\(\\d+,\\d+,\\d+,\\d+,\\d+,\\d+\\)";

    private static final String USER_COMMAND = "USER ";
    private static final String PASSWORD_COMMAND = "PASS ";
    private static final String PASSIVE_CONNECTION_COMMAND = "PASV";
    private static final String QUIT_COMMAND = "QUIT";
    private static final String SEND_POST_COMMAND = "PORT ";
    private static final String RETRIEVE_FILE_COMMAND = "RETR ";
    private static final String STORE_FILE_COMMAND ="STOR ";

    private static final String LOGIN_SUCCESSFUL_CODE = "230";
    private static final String SPECIFY_PASSWORD_CODE = "331";
    private static final String ENTERING_PASSIVE_MODE_CODE = "227";
    private static final String STARTING_DATA_TRANSFER_CODE = "150";
    private static final String OPERATION_SUCCESSFUL_CODE = "226";
    private static final String LOGIN_INCORRECT_CODE = "530";
    private static final String POST_COMMAND_SUCCESSFUL_CODE = "200";

    private final Socket manageSocket;
    private final BufferedReader commandIn;
    private final PrintWriter commandOut;

    private Socket dataSocket;
    private InputStream dataIn;
    private OutputStream dataOut;

    private ServerSocket serverSocket;

    private TypeOfConnection typeOfConnection;

    public QueryHandler(String host, int port) throws IOException {
        manageSocket = new Socket(host, port);
        commandIn = new BufferedReader(new InputStreamReader(manageSocket.getInputStream()));
        commandOut = new PrintWriter(manageSocket.getOutputStream(), true);
    }

    public boolean login(String login, String password) throws IOException, TimeoutException {
        sendCommand(USER_COMMAND + login);
        waitingCommandResponse(SPECIFY_PASSWORD_CODE);
        sendCommand(PASSWORD_COMMAND + password);
        return waitingCommandResponse(LOGIN_SUCCESSFUL_CODE,LOGIN_INCORRECT_CODE).startsWith(LOGIN_SUCCESSFUL_CODE);
    }

    public void chooseTypeOfConnection(TypeOfConnection typeOfConnection){
        this.typeOfConnection = typeOfConnection;
    }

    public String readFile(String pathToFile) throws IOException, TimeoutException {
        initDataChannel();
        sendCommand(RETRIEVE_FILE_COMMAND +pathToFile);
        if(typeOfConnection.equals(TypeOfConnection.ACTIVE)){
            dataSocket = serverSocket.accept();
            dataIn = dataSocket.getInputStream();
            dataOut = dataSocket.getOutputStream();
        }
        waitingCommandResponse(STARTING_DATA_TRANSFER_CODE);
        try (ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = dataIn.read(buffer)) != -1) {
                byteArrayOut.write(buffer, 0, bytesRead);

            }
            waitingCommandResponse(OPERATION_SUCCESSFUL_CODE);
            if(serverSocket!=null&&!serverSocket.isClosed()){
                serverSocket.close();
            }
            return byteArrayOut.toString("UTF-8");
        }
    }

    public void writeFile(String pathToFile,String fileContent) throws IOException, TimeoutException {
        initDataChannel();
        sendCommand(STORE_FILE_COMMAND+pathToFile);
        waitingCommandResponse(STARTING_DATA_TRANSFER_CODE);
        if(typeOfConnection.equals(TypeOfConnection.ACTIVE)){
            dataSocket = serverSocket.accept();
            dataIn = dataSocket.getInputStream();
            dataOut = dataSocket.getOutputStream();
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(dataOut)) {
            writer.write(fileContent);
            writer.flush();
        }
        waitingCommandResponse(OPERATION_SUCCESSFUL_CODE);
        if(serverSocket!=null&&!serverSocket.isClosed()){
            serverSocket.close();
        }
    }

    private void initDataChannel() throws IOException, TimeoutException {
        switch (typeOfConnection){
            case PASSIVE: {passiveConnect();break;}
            case ACTIVE: {activeConnect();break;}
        }
    }

    private void activeConnect() throws IOException, TimeoutException {
        serverSocket =  new ServerSocket(0);
        int[] port = portToXY(serverSocket.getLocalPort());
        sendCommand(SEND_POST_COMMAND+"127.0.0.1".replace(".",",")+","+port[0]+","+port[1]);
        waitingCommandResponse(POST_COMMAND_SUCCESSFUL_CODE);
    }
    public static int[] portToXY(int port) {
        int x = port / 256;
        int y = port % 256;
        return new int[]{x, y};
    }

    private void passiveConnect() throws IOException, TimeoutException {
        sendCommand(PASSIVE_CONNECTION_COMMAND);
        String line = waitingCommandResponse(ENTERING_PASSIVE_MODE_CODE);
        dataSocket = new Socket(getHostFromParts(parseIp(line)),getPortFromParts(parseIp(line)));
        dataIn = dataSocket.getInputStream();
        dataOut = dataSocket.getOutputStream();
    }

    private static int[] parseIp(String responseServer){
        int[] parts = new int[6];
        Pattern regex = Pattern.compile(IP_SERVER_PATTERN);
        Matcher matcher = regex.matcher(responseServer);
        if(matcher.find()){
            String[] partsStr = matcher.group().replace("(", "").replace(")", "").split(",");
            parts = Arrays.stream(partsStr)
                    .mapToInt(Integer::parseInt)
                    .toArray();
        }
        if (Arrays.stream(parts).allMatch(i -> i == 0)) {
            throw new IllegalArgumentException("Неверный формат IP указан в ответе от сервера: " + responseServer);
        }
        return parts;
    }

    private static String getHostFromParts(int[] parts){
        StringBuilder host = new StringBuilder();
        for(int i = 0; i < 4; i++){
            host.append(parts[i]).append(".");
        }
        host.deleteCharAt(host.length() - 1);
        return host.toString();
    }

    private static int getPortFromParts(int[] parts){
        return parts[4]*256+parts[5];
    }


    private String waitingCommandResponse(String... expectedCodes) throws IOException, TimeoutException {
        long startTime = System.currentTimeMillis();
        long timeoutMs = 5000;
        String line = "";
        while (true){
            if(commandIn.ready()){
                line = commandIn.readLine();
            }
            if(Arrays.stream(expectedCodes).anyMatch(line::startsWith)){
                break;
            }
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(line.startsWith("5")){
                throw new RuntimeException(line);
            }
            if (System.currentTimeMillis() - startTime > timeoutMs) {
                throw new TimeoutException("Вышло время ожидания ответа от сервера!");
            }
        }
        return line;
    }

    private void sendCommand(String command){
        commandOut.println(command);
        commandOut.flush();
    }

    @Override
    public void close() throws IOException {
        sendCommand(QUIT_COMMAND);
        commandIn.close();
        commandOut.close();
        manageSocket.close();
        if (dataSocket != null) {
            dataIn.close();
            dataOut.close();
            dataSocket.close();
        }
    }
}

