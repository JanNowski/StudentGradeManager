package rest.resources;

import rest.models.GradeModel;
import rest.models.StudentModel;
import rest.models.CourseModel;

import java.util.ArrayList;

/**
 * Created by Maks on 27.04.2017.
 */
public class DefaultResource {
    private final Students students;
    private final Courses courses;

    public DefaultResource() {
        this.students = new Students();
        this.courses = new Courses();
        this.initialize();
    }

    public ArrayList<StudentModel> initialize() {
        synchronized(students) {
            synchronized(courses) {
                students.addStudent(new StudentModel("Maks", "Marcinowski", "1993-03-24"));
                students.addStudent(new StudentModel("Jan", "Nowak", "1990-01-01"));
                students.addStudent(new StudentModel("Jerzy", "Kowalski", "1992-09-13"));

                courses.addCourse(new CourseModel("TP-SI", "T. Pawlak"));
                courses.addCourse(new CourseModel("PIT", "A. Jaszkiewicz"));
                courses.addCourse(new CourseModel("MiASI", "B. Walter"));

                students.addGrade(new GradeModel(5.0, courses.getCourses().get(0)), students.getStudents().get(0));
                students.addGrade(new GradeModel(5.0, courses.getCourses().get(1)), students.getStudents().get(0));
                students.addGrade(new GradeModel(2.0, courses.getCourses().get(0)), students.getStudents().get(1));
                students.addGrade(new GradeModel(5.0, courses.getCourses().get(1)), students.getStudents().get(1));
                students.addGrade(new GradeModel(3.0, courses.getCourses().get(2)), students.getStudents().get(2));
            }
        }

        synchronized(students) {
            return students.getStudents();
        }
    }
}
