package rest.controllers;

import rest.models.CourseModel;
import rest.models.GradeModel;
import rest.models.StudentModel;
import rest.resources.Courses;
import rest.resources.Students;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Maks on 27.04.2017.
 */

@Path("/")
public class GradeMethods {
    private final ArrayList<Double> gradeScale = new ArrayList<>(Arrays.asList(2.0, 3.0, 3.5, 4.0, 4.5, 5.0));
    private final Students students;
    private final Courses courses;

    public GradeMethods() {
        this.students = new Students();
        this.courses = new Courses();
    }

    private Boolean checkGrade(Double gradeToCheck) {
        Boolean isOneOfGrades = false;

        for(Double grade : gradeScale) {
            if(Objects.equals(grade, gradeToCheck)) isOneOfGrades = true;
        }

        return isOneOfGrades;
    }

    @Path("grades")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudentsGrades() {
        ArrayList<StudentModel> studentsList;
        synchronized(students) {
            studentsList = students.getStudents();
        }

        return Response.ok(studentsList).build();
    }

    @Path("grades/{gradeID}")
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getGrade(@PathParam("gradeID") final long gradeID) {
        GradeModel grade;
        synchronized(students) {
            grade = students.getGrade(gradeID);
        }

        if(grade == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        }

        return Response.ok(grade).build();
    }

    @Path("grades/{gradeID}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateGrade(@PathParam("gradeID") final long gradeID, final GradeModel grade) {


        GradeModel gradeObject;
        synchronized(students) {
            gradeObject = students.getGrade(gradeID);
        }

        if(gradeObject == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Ocena nie istnieje").build());
        }

        if(!checkGrade(grade.getGrade())) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Ocena jest niepoprawna").build());
        }

        if(grade.getCourse() != null) {
            CourseModel course;
            synchronized (courses) {
                course = courses.getCourse(grade.getCourse().getCourseID());

            }

            if (course == null) {
                throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Przedmiot nie istnieje").build());
            }

            gradeObject.setCourse(course);
        }

        synchronized (students) {
            //if(grade.getCourse() != null) gradeObject.setCourse(grade.getCourse());
            if(grade.getGrade() != null) gradeObject.setGrade(grade.getGrade());
            if(grade.getCourse() != null || grade.getGrade() != null) gradeObject.setDate(new Date().toString());
        }

        return Response.ok(gradeObject).build();
    }

    @Path("grades/{gradeID}")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response removeGrade(@PathParam("gradeID") final long gradeID, @Context UriInfo uriInfo) {
        GradeModel gradeObject;
        synchronized(students) {
            gradeObject = students.getGrade(gradeID);
        }

        if(gradeObject == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Ocena nie istnieje").build());
        }

        synchronized (students) {
            students.removeGrade(gradeID);
        }

        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(gradeObject.getGradeID())).build();
        //return Response.created(uri).entity(gradeObject).build();
        return Response.ok(gradeObject).build();
    }
}
