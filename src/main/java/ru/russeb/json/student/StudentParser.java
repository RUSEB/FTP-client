package ru.russeb.json.student;

import ru.russeb.model.Student;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StudentParser {
    private final static String JSON_PATTERN = "\\{\"students\":\\[(\\{\"id\":\\d+,\"name\":\"[^\"]*\",\"course\":-?\\d+,\"directionOfStudy\":\"[^\"]*\",\"faculty\":\"[^\"]*\"}(?:,\\{\"id\":\\d+,\"name\":\"[^\"]*\",\"course\":\\d+,\"directionOfStudy\":\"[^\"]*\",\"faculty\":\"[^\"]*\"})*)?]}";
    private final static String STUDENT_PATTERN = "\\{\"id\":(\\d+),\"name\":\"([^\"]*)\",\"course\":(-?\\d+),\"directionOfStudy\":\"([^\"]*)\",\"faculty\":\"([^\"]*)\"\\}";
    private final static String JSON_WITHOUT_SPACE_PATTERN = "(?=([^\"]*\"[^\"]*\")*[^\"]*$)\\s+";

    public static JsonResult parse(String json) throws JsonParseException {
        String jsonWithoutLineFeed =
                json.replace("\n","").replaceAll(JSON_WITHOUT_SPACE_PATTERN, "");

        if(invalidJson(jsonWithoutLineFeed)){
            throw new JsonParseException();
        }
        Matcher matcher = getMatcherWithStudents(jsonWithoutLineFeed);
        return buildStudentsFromMatcher(matcher);
    }

    private static boolean invalidJson(String json){
        Pattern pattern = Pattern.compile(JSON_PATTERN);
        Matcher matcher = pattern.matcher(json);
        return !matcher.matches();
    }

    private static Matcher getMatcherWithStudents(String json){
        Pattern pattern = Pattern.compile(STUDENT_PATTERN);
        return pattern.matcher(json);
    }
    private static JsonResult buildStudentsFromMatcher(Matcher matcher) {
        Map<Integer,Student> studentMap = new HashMap<>();
        List<Student> invalidStudentList = new ArrayList<>();
        Student student;
        while (matcher.find()) {
            int id = Integer.parseInt(matcher.group(1));
            String name = matcher.group(2);
            int course = Integer.parseInt(matcher.group(3));
            String directionOfStudy = matcher.group(4);
            String faculty = matcher.group(5);

            if (invalidId(id)) {
                student = new Student(invalidStudentList.size()+1,name);
                student.setCourse(course);
                student.setDirectionOfStudy(directionOfStudy);
                student.setFaculty(faculty);
                invalidStudentList.add(student);
            }
            else{
                student = new Student(id, name);
                student.setCourse(course);
                student.setDirectionOfStudy(directionOfStudy);
                student.setFaculty(faculty);
                studentMap.put(student.getId(), student);
            }
        }
        return new JsonResult(studentMap,invalidStudentList);
    }

    private static boolean invalidId(int id){
        return id <= 0;
    }


    public static String writeStudentsAtJson(Map<Integer, Student> studentMap) {
        StringBuilder studentArrayJson = new StringBuilder();

        studentMap.forEach((key, value) -> {
            studentArrayJson.append(getStudentJson(value)).append(",\n");
        });

        if (studentArrayJson.length() > 0) {
            studentArrayJson.deleteCharAt(studentArrayJson.length() - 2);
        }
        return String.format("{\n  \"students\": [\n%s\n  ]\n}", studentArrayJson);
    }

    private static String getStudentJson(Student student) {
        return String.format("    {\n"
                        + "      \"id\": %d,\n"
                        + "      \"name\": \"%s\",\n"
                        + "      \"course\": %d,\n"
                        + "      \"directionOfStudy\": \"%s\",\n"
                        + "      \"faculty\": \"%s\"\n"
                        + "    }",
                student.getId(),
                (Student.NO_INFO.equals(student.getName()) ? "" : student.getName()),
                (student.getCourse()),
                (Student.NO_INFO.equals(student.getDirectionOfStudy()) ? "" : student.getDirectionOfStudy()),
                (Student.NO_INFO.equals(student.getFaculty()) ? "" : student.getFaculty()));
    }

}