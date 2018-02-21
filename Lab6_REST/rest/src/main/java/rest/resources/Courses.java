package rest.resources;

import rest.models.CourseModel;

import java.util.ArrayList;

/**
 * Created by Maks on 27.04.2017.
 */
public final class Courses {
    private static ArrayList<CourseModel> courses = new ArrayList<>();

    public  Courses() {}

    public ArrayList<CourseModel> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<CourseModel> courses) {
        Courses.courses = courses;
    }

    public CourseModel getCourse(long id) {
        CourseModel course = null;
        for(CourseModel c : courses) {
            if(c.getCourseID()==id) course = c;
        }

        return course;
    }

    public void addCourse(CourseModel newCourse) {
        courses.add(newCourse);
    }

    public void removeCourse(CourseModel course) {
        courses.remove(course);
    }
}
