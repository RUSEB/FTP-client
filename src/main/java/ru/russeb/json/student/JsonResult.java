package ru.russeb.json.student;

import ru.russeb.model.Student;

import java.util.List;
import java.util.Map;

public class JsonResult {
    private Map<Integer, Student> studentMap;
    private List<Student> invalidStudentObjects;

    public JsonResult(Map<Integer,Student> studentMap,List<Student> invalidStudentObjects){
        this.studentMap = studentMap;
        this.invalidStudentObjects = invalidStudentObjects;
    }

    public Map<Integer, Student> getStudentMap() {
        return studentMap;
    }
    public List<Student> getInvalidStudentObjects(){
        return invalidStudentObjects;
    }

    public boolean hasInvalidStudentObjects(){
        return !invalidStudentObjects.isEmpty();
    }
}
