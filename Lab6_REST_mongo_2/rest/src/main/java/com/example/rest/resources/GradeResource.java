package com.example.rest.resources;

import com.example.rest.models.Course;
import com.example.rest.models.Grade;
import com.example.rest.models.Student;
import com.example.rest.mongo.DatastoreUtil;
import com.example.rest.mongo.Utils;
import org.mongodb.morphia.Datastore;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;



@Path("/grades")
public class GradeResource {
    private final ArrayList<Double> gradeScale = new ArrayList<>(Arrays.asList(2.0, 3.0, 3.5, 4.0, 4.5, 5.0));

    public GradeResource() {}

    private Boolean checkGrade(Double gradeToCheck) {
        Boolean isOneOfGrades = false;

        for(Double grade : gradeScale) {
            if(Objects.equals(grade, gradeToCheck)) isOneOfGrades = true;
        }

        return isOneOfGrades;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudentsGrades() {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        ArrayList<Student> studentsList = (ArrayList<Student>) datastore.find(Student.class).asList();

        return Response.ok(studentsList).build();
    }

    @Path("/{gradeID}")
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getGrade(@PathParam("gradeID") final String gradeID) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        ArrayList<Student> students = (ArrayList<Student>) datastore.find(Student.class).asList();

        Grade grade = Utils.getGrade(students, gradeID);

        if(grade == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        }

        return Response.ok(grade).build();
    }

    @Path("/{gradeID}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateGrade(@PathParam("gradeID") final String gradeID, final Grade grade) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        ArrayList<Student> students = (ArrayList<Student>) datastore.find(Student.class).asList();

        Grade gradeObject = Utils.getGrade(students, gradeID);

        if(gradeObject == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Ocena nie istnieje").build());
        }

        if(!checkGrade(grade.getGrade())) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Ocena jest niepoprawna").build());
        }

        if(grade.getCourse() != null) {
            //Course course = datastore.find(Course.class).field("courseID").equal(grade.getCourse().getCourseID()).get();
            Course course = null;
            ArrayList<Course> courses = (ArrayList<Course>) datastore.find(Course.class).asList();
            for(Course c : courses) {
                if(Objects.equals(c.getCourseID(), grade.getCourse().getCourseID())) course = c;
            }


            if(course == null) {
                throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Przedmiot nie istnieje").build());
            }
        }
        if(grade.getCourse() != null) gradeObject.setCourse(grade.getCourse());
        if(grade.getGrade() != null) gradeObject.setGrade(grade.getGrade());
        if(grade.getDate() != null) {
            gradeObject.setDate(grade.getDate());
        }
        else{
            gradeObject.setDate(new Date());
        }

        datastore.save(students);

        return Response.ok(gradeObject).build();
    }

    @Path("/{gradeID}")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeGrade(@PathParam("gradeID") final String gradeID, @Context UriInfo uriInfo) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        ArrayList<Student> students = (ArrayList<Student>) datastore.find(Student.class).asList();

        Grade gradeObject = Utils.getGrade(students, gradeID);

        if(gradeObject == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Ocena nie istnieje").build());
        }

        Student student = Utils.getStudentWithGrade(students, gradeID);

        student.getGrades().remove(gradeObject);

        datastore.save(student);

        return Response.ok(gradeObject).build();
    }
}
