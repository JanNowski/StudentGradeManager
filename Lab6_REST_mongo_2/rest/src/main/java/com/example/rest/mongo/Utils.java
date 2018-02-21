package com.example.rest.mongo;

import com.example.rest.models.Grade;
import com.example.rest.models.Student;

import java.util.ArrayList;
import java.util.Objects;


public final class Utils {
    public static Grade getGrade(ArrayList<Student> students, String gradeID) {
        Grade grade = null;
        for(Student s : students) {
            for(Grade g : s.getGrades()) {
                if(Objects.equals(g.getGradeID(), gradeID)) grade = g;
            }
        }

        return grade;
    }

    public static Student getStudentWithGrade(ArrayList<Student> students, String gradeID) {
        Student student = null;
        for(Student s : students) {
            for(Grade g : s.getGrades()) {
                if(Objects.equals(g.getGradeID(), gradeID)) student = s;
            }
        }

        return student;
    }

    public static void removeCourseGrades(Student student, String courseID) {
        ArrayList<Grade> grades = new ArrayList<>();

        for(Grade g : student.getGrades()) {
            if(g.getCourse().getCourseID().equals(courseID)) grades.add(g);
        }

        for(Grade gradeToRemove : grades) {
            student.getGrades().remove(gradeToRemove);
        }
    }
}
