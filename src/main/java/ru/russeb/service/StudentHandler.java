package ru.russeb.service;

import ru.russeb.model.Student;

import java.util.*;
import java.util.stream.Collectors;

public class StudentHandler {
    private final Map<Integer,Student> studentMap;

    public StudentHandler(Map<Integer,Student> students) {
        studentMap = students;
    }

    public Optional<Student> addStudent(String name) {
        if (name == null) {
            return Optional.empty();
        }

        Student student = new Student(generateId(), name);
        studentMap.put(student.getId(), student);
        return Optional.of(student);
    }


    private int generateId(){
        int id = 1;
        while (studentMap.get(id)!=null){
            id++;
        }
        return id;
    }

    public Map<Integer, Student> getStudentMap() {
        return studentMap;
    }

    public List<Student> getStudentsAsListSortByName(String name) {
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

}
