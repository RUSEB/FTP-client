package ru.russeb.start;

import ru.russeb.model.Student;
import ru.russeb.service.client.FtpClientHandler;
import ru.russeb.service.client.LoginIncorrectException;
import ru.russeb.service.client.TypeOfConnection;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class StartClient {

    private static FtpClientHandler ftpClientHandler;

    public static void main(String[] args) {
        ftpClientHandler = FtpClientHandler.get();
        initClient();
        menu();
    }

    private static void initClient() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Для начала работы необходимо запонить поля , необходимые для подключения к FTP-серверу.");
        ftpClientHandler.setHost(requestHost(scanner));
        ftpClientHandler.setPort(requestPort(scanner));
        ftpClientHandler.setLogin(requestLogin(scanner));
        ftpClientHandler.setPassword(requestPassword(scanner));
        ftpClientHandler.setTypeOfConnection(requestTypeOfConnection(scanner));
        ftpClientHandler.setPathToFile(requestPathToFile(scanner));
        System.out.println(ftpClientHandler);
        System.out.println("Настройки можно изменить в меню. Переходим в меню...\n");

    }

    private static void menu(){
        int commandCode = -1;
        Scanner scanner = new Scanner(System.in);
        while (commandCode != 6) {
            System.out.println("-------------------------Меню-------------------------");
            System.out.println("Пожалуйста, введите число:");
            System.out.println("1 - для получения всего списка всех студентов");
            System.out.println("2 - для получения студента по ID");
            System.out.println("3 - для добавления студента");
            System.out.println("4 - для удаления студента по ID");
            System.out.println("5 - для открытия настроек");
            System.out.println("6 - для завершения работы");
            System.out.print("=> ");
            System.err.flush();

            try{
                commandCode = scanner.nextInt();
                System.out.println("Пожалуйста, ожидайте...");
            }catch (InputMismatchException e){
                System.err.println("Ошибка ввода, пожалуйста, введите число!");
                scanner.nextLine();
            }

            switch (commandCode) {
                case 1:{printListOfStudents();break;}
                case 2:{printStudentById();break;}
                case 3:{addStudent();break;}
                case 4:{deleteStudentById();break;}
                case 5:{settings();break;}
                case 6:{break;}
                default:{
                    System.err.println("Неизвестная команда.");
                }
            }
        }
        System.out.println("Завершение работы...");
    }

    private static void printListOfStudents(){
        try {
            List<Student> studentList = ftpClientHandler.getStudents();
            if(studentList.isEmpty()){
                System.out.println("Список всех студентов пуст.");
            }else {
                System.out.println("Список студенов, отсортированный по имени:");
                for(Student student : studentList){
                    System.out.println(student);
                }
                System.out.println();
            }
        } catch (Exception e) {
            handleException(e);
        }
    }

    private static void printStudentById(){
        System.out.println("Вывод студента по ID");
        int id = requestId();
        try {
            Optional<Student> studentOptional = ftpClientHandler.getStudentById(id);
            if(studentOptional.isPresent()){
                System.out.println("Студент с ID -" + id);
                System.out.println(studentOptional.get());
            }else{
                System.out.println("Студент с ID - "+ id + " не был найден.");
            }
        } catch (Exception e){
            handleException(e);
        }
        System.out.println();
    }

    private static void addStudent(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добавление студента, если данных нет, можете оставить пустую строку:");
        System.out.print("Пожалуйста, введите имя студента: ");
        String name = scanner.nextLine();

        int course = getIntInput(scanner,"Пожалуйста, введите курс обучения студента: ");
        System.out.print("Пожалуйста, введите направление обучения студента: ");
        String directionOfStudy = scanner.nextLine();
        System.out.print("Пожалуйста, введите факультет обучения студента: ");
        String faculty = scanner.nextLine();

        Student student = new Student(1,name);
        student.setCourse(course);
        student.setDirectionOfStudy(directionOfStudy);
        student.setFaculty(faculty);
        try{
            int id = ftpClientHandler.addStudent(student);
            System.out.println("Студент был успешно добавлен с ID - "+id);
        }catch (Exception e){
            handleException(e);
        }
    }

    private static void deleteStudentById(){
        System.out.println("Удаление студента по ID");
        int id = requestId();

        try {
            Optional<Student> studentOptional = ftpClientHandler.deleteStudentById(id);
            if(studentOptional.isPresent()){
                System.out.println("Студент с ID - "+ id + " был успешно удален.");
            }else{
                System.out.println("Студент с ID - "+ id + " не был найден.");
            }
        } catch (Exception e){
            handleException(e);
        }
    }

    private static void settings() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--------------------Настройки--------------------");
        System.out.println(ftpClientHandler);
        System.out.println("Что Вы хотите изменить? Введите число:");
        System.out.println("1 - Хост сервера");
        System.out.println("2 - Порт сервера");
        System.out.println("3 - Логин");
        System.out.println("4 - Пароль");
        System.out.println("5 - Тип подключения");
        System.out.println("6 - Путь до файла на сервере");
        System.out.println("7 - Начать ввод данных подключения заново");
        System.out.println("8 - Назад в меню");
        System.out.print("=> ");
        boolean commandCorrect = false;
        while (!commandCorrect) {
            try {
                int command = scanner.nextInt();
                scanner.nextLine();
                commandCorrect = true;
                switch (command) {
                    case 1: {ftpClientHandler.setHost(requestHost(scanner));break;}
                    case 2: {ftpClientHandler.setPort(requestPort(scanner));break;}
                    case 3: {ftpClientHandler.setLogin(requestLogin(scanner));break;}
                    case 4: {ftpClientHandler.setPassword(requestPassword(scanner));break;}
                    case 5: {ftpClientHandler.setTypeOfConnection(requestTypeOfConnection(scanner));break;}
                    case 6: {ftpClientHandler.setPathToFile(requestPathToFile(scanner));break;}
                    case 7: {initClient();break;}
                    case 8: {break;}
                    default: {System.err.print("Неизвестная команда, введите заново: ");break;}
                }
            } catch (InputMismatchException e) {
                System.err.print("Необходимо ввести число: ");
                scanner.nextLine();
            }
        }
        System.out.println();
        menu();
    }

    private static void handleException(Exception e) {
        if (e instanceof IOException) {
            System.err.println("Ошибка чтения/записи, повторите попытку позже: " + e);
        } else if (e instanceof TimeoutException) {
            System.err.println("Сервер не отвечает, повторите попытку позже: " + e);
        } else if (e instanceof LoginIncorrectException) {
            System.err.println("Логин или пароль неверны! Измените их в настройках.");
        } else{
            System.err.println("Произошла неизвестная ошибка: " + e);
        }
        System.out.println();
    }

    private static int requestId(){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Пожалуйста, введите ID: ");
        while (true){
            try {
                return scanner.nextInt();
            }catch (InputMismatchException e){
                System.err.print("Ошибка! Пожалуйста, введите число: ");
                scanner.nextLine();
            }
        }
    }


    private static String requestHost(Scanner scanner){
        return getStringInput(scanner, "Пожалуйста, введите хост сервера (пример - 127.0.0.1): ");
    }

    private static int requestPort(Scanner scanner){
        return getIntInput(scanner, "Пожалуйста, введите порт сервера (пример - 21): ");
    }

    private static String requestLogin(Scanner scanner){
        return getStringInput(scanner, "Пожалуйста, введите логин: ");
    }

    private static String requestPassword(Scanner scanner){
        return getStringInput(scanner, "Пожалуйста, введите пароль: ");
    }

    private static String requestPathToFile(Scanner scanner){
        return getStringInput(scanner,"Введите путь к файлу (пример - /information/students.json): ");
    }

    private static TypeOfConnection requestTypeOfConnection(Scanner scanner) {
        System.out.print("Нужно выбрать тип соеднинения к серверу, пожалуйста, введите число:\n1 - для выбора пассивного режима\n2 - для выбора активного режима\n=> ");
        while (true){
            try {
                int type = scanner.nextInt();
                switch (type) {
                    case 1:
                        return TypeOfConnection.PASSIVE;
                    case 2:
                        return TypeOfConnection.ACTIVE;
                    default:
                        System.err.print("Некорректный выбор. Пожалуйста, попробуйте еще раз: ");

                }
            } catch (InputMismatchException e) {
                System.err.println("Некорректный ввод. Пожалуйста, введите число: ");
                scanner.nextLine();
            }finally {
                scanner.nextLine();
            }
        }
    }

    private static String getStringInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int getIntInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String str = scanner.nextLine();
        if(str.isEmpty()){
            return 0;
        }
        while (true){
            try {
                return Integer.parseInt(str);
            }catch (NumberFormatException e){
                System.err.print("Ошибка, необходимо ввести число: ");
                str = scanner.nextLine();
            }
        }
    }


}

