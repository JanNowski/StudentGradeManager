package com.example.rest.mongo;

import com.example.rest.models.Counter;
import com.example.rest.models.Course;
import com.example.rest.models.Grade;
import com.example.rest.models.Student;
import com.example.rest.resources.CounterResource;
import org.mongodb.morphia.Datastore;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DefaultData {
    private static List<Student> students = new ArrayList<>();
    private static List<Course> courses = new ArrayList<>();

    public static void initializeData() throws ParseException {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        if(datastore.getCount(Student.class) == 0) {
            initializeStudents(datastore);
        }

        if(datastore.getCount(Course.class) == 0) {
            initializeCourses(datastore);
            initializeGrades(datastore);
        }
    }

    private static void initializeStudents(Datastore datastore) throws ParseException {
        Counter counter = new Counter("students");
        datastore.save(counter);

        students.add(new Student(CounterResource.instance.getSeq("students"), "Jan", "Kowalski", new Date(1994-1900, 7-1, 10)));
        students.add(new Student(CounterResource.instance.getSeq("students"), "Szymon", "Nowak", new Date(1995-1900, 2-1, 20)));
        students.add(new Student(CounterResource.instance.getSeq("students"), "Tadeusz", "Kaczmarek", new Date(1984-1900, 12-1, 29)));
        datastore.save(students);
    }

    private static void initializeCourses(Datastore datastore) {
        courses.add(new Course("MIASI", "Bartosz Walter"));
        courses.add(new Course("TPAL", "Piotr Zielniewicz"));
        courses.add(new Course("TPSI", "Tomasz Pawlak"));
        datastore.save(courses);
    }

    private static void initializeGrades(Datastore datastore) throws ParseException {
        datastore.update(students.get(0), datastore.createUpdateOperations(Student.class).add("grades", new Grade(5.0, courses.get(0))));
        datastore.update(students.get(0), datastore.createUpdateOperations(Student.class).add("grades", new Grade(3.0, courses.get(1))));
        datastore.update(students.get(1), datastore.createUpdateOperations(Student.class).add("grades", new Grade(2.0, courses.get(0))));
        datastore.update(students.get(1), datastore.createUpdateOperations(Student.class).add("grades", new Grade(4.5, courses.get(2))));
        datastore.update(students.get(2), datastore.createUpdateOperations(Student.class).add("grades", new Grade(3.5, courses.get(0))));
        datastore.update(students.get(2), datastore.createUpdateOperations(Student.class).add("grades", new Grade(5.0, courses.get(2))));
    }
}
