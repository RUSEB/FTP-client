package ru.russeb.model;

public class Student {
    public final static String NO_INFO = "Инфромация отсутствует";
    public final static int NO_COURSE = 0;

    private final int id;
    private String name;
    private int course = NO_COURSE;
    private String faculty = NO_INFO;
    private String directionOfStudy = NO_INFO;

    public Student(int id, String name) throws RuntimeException{
        if(id<=0){
            throw new RuntimeException("ID должен быть больше нуля!");
        }
        this.id = id;
        if(name==null||name.isEmpty()){
            this.name = NO_INFO;
        }else{
            this.name = name;
        }
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public int getCourse(){
        return course;
    }

    public String getFaculty(){
        return faculty;
    }

    public String getDirectionOfStudy(){
        return directionOfStudy;
    }

    public void setCourse(int course){
        if(course<0){
            this.course = NO_COURSE;
        }else {
            this.course = course;
        }
    }

    public void setFaculty(String faculty){
        if(faculty==null||faculty.isEmpty()){
            this.faculty = NO_INFO;
        }else {
            this.faculty = faculty;
        }

    }
    public void setDirectionOfStudy(String directionOfStudy){
        if(directionOfStudy==null||directionOfStudy.isEmpty()){
            this.directionOfStudy = NO_INFO;
        }else {
            this.directionOfStudy = directionOfStudy;
        }
    }

    public void setName(String name){
        if(name==null||name.isEmpty()){
            this.name = NO_INFO;
        }else{
            this.name = name;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Student student = (Student) obj;
        return id ==student.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "ID - " + id + ": " +
                (NO_INFO.equals(name) ? "(Имя неизвестно), " : name + ", студент ") +
                (course==NO_COURSE ? "(курс неизвестен), " : course+"-го курса, ")  + "обучающийся на направлении \"" + directionOfStudy +
                "\" на факультете - \"" + faculty + "\".";
    }
}
