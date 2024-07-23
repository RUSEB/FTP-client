package ru.russeb.service;

import ru.russeb.model.Student;

import java.util.*;
import java.util.stream.Collectors;

public class StudentHandler {

    private final Map<Integer,Student> studentMap;

    public StudentHandler(Map<Integer,Student> students) {
        studentMap = students;
    }

    public int addStudent(Student student) {
        if (student == null) {
            throw new RuntimeException("Student can't be null");
        }
        Student newStudent = new Student(generateId(),
                student.getName()
                        .replace("\"","")
                        .replace("\\","")
                        .replace("\n",""));
        newStudent.setCourse(student.getCourse());
        newStudent.setDirectionOfStudy(
                student.getDirectionOfStudy()
                        .replace("\"","")
                        .replace("\\","")
                        .replace("\n",""));
        newStudent.setFaculty(
                student.getFaculty()
                        .replace("\"","")
                        .replace("\\","")
                        .replace("\n",""));
        studentMap.put(newStudent.getId(), newStudent);
        return newStudent.getId();
    }

    public Map<Integer, Student> getStudentMap() {
        return studentMap;
    }

    public List<Student> getStudentsAsListSortByName() {
        return studentMap.values().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public Optional<Student> getStudentById(int id){
        return Optional.ofNullable(studentMap.get(id));
    }

    public Optional<Student> deleteStudentById(int id){
        return Optional.ofNullable(studentMap.remove(id));
    }

    private int generateId(){
        int id = 1;
        while (studentMap.get(id)!=null){
            id++;
        }
        return id;
    }
}
