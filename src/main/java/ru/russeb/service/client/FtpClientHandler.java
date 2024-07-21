package ru.russeb.service.client;

import ru.russeb.json.student.JsonParseException;
import ru.russeb.json.student.JsonResult;
import ru.russeb.json.student.StudentParser;
import ru.russeb.model.Student;
import ru.russeb.service.StudentHandler;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class FtpClientHandler {


    private  String login;
    private  String password;
    private  String host;
    private  int port;
    private TypeOfConnection typeOfConnection;
    private String pathToFile = "/information/students.json";

    private static FtpClientHandler ftpClientHandler;

    private FtpClientHandler(){
    }

    public static FtpClientHandler get(){
        if(ftpClientHandler ==null){
            ftpClientHandler = new FtpClientHandler();
        }
        return ftpClientHandler;
    }

    public void setHost(String host){
        this.host = host;
    }

    public void setPort(int port){
        this.port = port;
    }

    public void setLogin(String login){
        this.login = login;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setPathToFile(String pathToFile){
        this.pathToFile = pathToFile;
    }

    public void setTypeOfConnection(TypeOfConnection typeOfConnection){
        this.typeOfConnection = typeOfConnection;
    }

    public List<Student> getStudents() throws IOException, TimeoutException, LoginIncorrectException {
        try(QueryHandler queryHandler = connect()){
            JsonResult jsonResult = parseJson(queryHandler.readFile(pathToFile));
            processingJsonResult(queryHandler,jsonResult);
            StudentHandler studentHandler = new StudentHandler(jsonResult.getStudentMap());
            return studentHandler.getStudentsAsListSortByName();
        }
    }

    public int addStudent(Student student) throws IOException, TimeoutException, LoginIncorrectException {
        try(QueryHandler queryHandler = connect()){
            StudentHandler studentHandler =  getStudentHandler();
            int id = studentHandler.addStudent(student);
            save(queryHandler,studentHandler);
            return id;
        }
    }

    public Optional<Student> getStudentById(int id) throws IOException, TimeoutException, LoginIncorrectException {
        return getStudentHandler().getStudentById(id);
    }

    public Optional<Student> deleteStudentById(int id) throws IOException, TimeoutException, LoginIncorrectException {
        try(QueryHandler queryHandler = connect()){
            JsonResult jsonResult = parseJson(queryHandler.readFile(pathToFile));
            processingJsonResult(queryHandler,jsonResult);
            StudentHandler studentHandler = new StudentHandler(jsonResult.getStudentMap());
            Optional<Student> studentOptional = studentHandler.deleteStudentById(id);
            if(studentOptional.isPresent()){
                save(queryHandler,studentHandler);
            }
            return studentOptional;
        }
    }

    private StudentHandler getStudentHandler() throws IOException, TimeoutException, LoginIncorrectException {
        try(QueryHandler queryHandler = connect()){
            JsonResult jsonResult = parseJson(queryHandler.readFile(pathToFile));
            processingJsonResult(queryHandler,jsonResult);
            return new StudentHandler(jsonResult.getStudentMap());
        }
    }

    private JsonResult parseJson(String json) throws LoginIncorrectException, IOException, TimeoutException {
        try {
            return StudentParser.parse(json);
        } catch (JsonParseException e) {
            System.err.println("При обработке файла Json произошла ошибка: файл поврежден:");
            System.err.println(json);
            System.err.println("Создание пустого списка и добавление его в файл...");
            saveEmptyJson();
            System.err.println("Список успешно создан!");
            return new JsonResult(new HashMap<>(),new ArrayList<>());
        }
    }

    private void processingJsonResult(QueryHandler queryHandler,JsonResult jsonResult) throws IOException, TimeoutException {
        if(jsonResult.hasInvalidStudentObjects()){
            warnInvalidStudentsObjects(jsonResult.getInvalidStudentObjects());
            deleteInvalidInformationFromServer(queryHandler,jsonResult.getStudentMap());
        }
    }
    private void warnInvalidStudentsObjects(List<Student> invalidStudentObjects){
        System.err.println("При получении данных была найдена инфромация о студентах, которые имеют некорректный ID:");
        printInvalidStudentObjects(invalidStudentObjects);
    }

    private void deleteInvalidInformationFromServer(QueryHandler queryHandler,Map<Integer,Student> studentMap) throws IOException, TimeoutException {
        System.err.println("Проходит процесс удаления этих данных из сервера...");
        queryHandler.writeFile(pathToFile,StudentParser.writeStudentsAtJson(studentMap));
        System.err.println("Некорректные данные удалены!");
    }

    private void printInvalidStudentObjects(List<Student> invalidStudentObjects){
        for(Student student:invalidStudentObjects){
            System.err.println(student);
        }
    }

    private void save(QueryHandler queryHandler,StudentHandler studentHandler) throws IOException, TimeoutException {
        queryHandler.writeFile(pathToFile,StudentParser.writeStudentsAtJson(studentHandler.getStudentMap()));
    }

    private QueryHandler connect() throws IOException, TimeoutException,LoginIncorrectException {
        QueryHandler queryHandler = new QueryHandler(host,port);
        if(!queryHandler.login(login,password)){
            throw new LoginIncorrectException();
        }
        queryHandler.chooseTypeOfConnection(typeOfConnection);
        return queryHandler;
    }

    private void saveEmptyJson() throws LoginIncorrectException, IOException, TimeoutException {
        try(QueryHandler queryHandler = connect()) {
            Map<Integer,Student> studentMap = new HashMap<>();
            queryHandler.writeFile(pathToFile,StudentParser.writeStudentsAtJson(studentMap));
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Сохраненная информация для подключения к FTP-серверу")
                .append("\n")
                .append("IP сервера - ").append(host).append(":").append(port)
                .append("\n")
                .append("Логин - ").append(login == null ? "" : login)
                .append("\n")
                .append("Пароль - ").append(password == null ? "" : password)
                .append("\n")
                .append("Тип подключения к FTP-серверу - ");
        switch (typeOfConnection){
            case ACTIVE: {stringBuilder.append("активный режим").append("\n");break;}
            case PASSIVE:{stringBuilder.append("пассивный режим").append("\n");break;}
        }
        stringBuilder.append("Файл с данными должен находиться в ").append(pathToFile);
        return stringBuilder.toString();
    }
}
