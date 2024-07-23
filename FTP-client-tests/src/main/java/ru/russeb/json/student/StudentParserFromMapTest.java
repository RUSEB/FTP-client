package ru.russeb.json.student;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.russeb.model.Student;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class StudentParserFromMapTest {

    private static final Logger log = Logger.getLogger(StudentParserFromMapTest.class.getName());

    @Test(dataProvider = "MapOfStudent")
    public void testParseMapOfStudent(Map<Integer, Student> map) throws JsonParseException, MapParseException {
        log.info("Testing parseMapOfStudent with Map: " + map.toString());
        String json = StudentParser.writeStudentsAtJson(map);
        StudentParser.parse(json);
        log.info("OK\n");
    }

    @Test(expectedExceptions = MapParseException.class)
    public void testMapException() throws MapParseException {
        Student student = new Student(1,"\"Тут не должно быть \"");
        student.setFaculty("\\");
        student.setCourse(1);
        student.setDirectionOfStudy("\n");
        Map<Integer, Student> map = new HashMap<>();
        map.put(1,student);
        log.info("Testing parseMapException with Map: " + map.toString());
        StudentParser.writeStudentsAtJson(map);

    }

    @DataProvider(name = "MapOfStudent")
    public Object[][] getMapOfStudent(){
        return new Object[][]{
                new HashMap[]{new HashMap<Integer, Student>()},
                new HashMap[]{new HashMap<Integer, Student>() {{
                    put(1, new Student(1, "TestStudentName"));
                }}},
                new HashMap[]{new HashMap<Integer, Student>() {{
                    put(1, new Student(1, "TestStudentName1"));
                    put(2, new Student(2, "TestStudentName2"));
                    put(3, new Student(3, "TestStudentName3"));
                    put(4, new Student(4, "TestStudentName4"));
                }}}};
    }

}
