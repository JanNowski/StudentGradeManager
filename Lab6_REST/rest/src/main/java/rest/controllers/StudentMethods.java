package rest.controllers;

import rest.models.StudentModel;
import rest.resources.Students;
import rest.models.CourseModel;
import rest.models.GradeModel;
import rest.resources.Courses;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Maks on 27.04.2017.
 */

@Path("/students")
public class StudentMethods {
    private final ArrayList<Double> gradeScale = new ArrayList<>(Arrays.asList(2.0, 3.0, 3.5, 4.0, 4.5, 5.0));

    private final Students students;
    private final Courses courses;

    public StudentMethods() {
        this.students = new Students();
        this.courses = new Courses();
    }

    private Boolean checkGrade(Double gradeToCheck) {
        Boolean isOneOfGrades = false;

        for(Double grade : gradeScale) {
            if(Objects.equals(grade, gradeToCheck)) {
                isOneOfGrades = true;
            }
        }

        return isOneOfGrades;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ArrayList<StudentModel> getStudents() {
        ArrayList<StudentModel> studentsList;
        synchronized(students) {
            studentsList = students.getStudents();
        }

        return studentsList;
        //return Response.ok(studentsList).build();
    }

    @Path("/{index}/grades")
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudentGrades(@PathParam("index") final Long studentIndex) {
        StudentModel student;
        synchronized(students) {
            student = students.getStudentByIndex(studentIndex);
        }

        if(student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        }

        return Response.ok(student.getGrades()).build();
    }

    //@Path("/{index}/courses/{courseID}/grades")
    @Path("/{index}/grades")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    /*public Response createGrade(@PathParam("index") final Long studentIndex, @PathParam("courseID") final long courseID,
                                final GradeModel grade, @Context UriInfo uriInfo) {*/
    public Response createGrade(@PathParam("index") final Long studentIndex,
                                final GradeModel grade, @Context UriInfo uriInfo) {
        StudentModel student;
        synchronized(students) {
            student = students.getStudentByIndex(studentIndex);
        }

        if(student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student doesn't exist").build());
        }

        CourseModel course;
        synchronized(courses) {
            course = courses.getCourse(grade.getCourse().getCourseID());
        }

        if(course == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Przedmiot nie istieje").build());
        }

        if(!checkGrade(grade.getGrade())) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Ocena niepoprawna").build());
        }

        GradeModel gradeObject = new GradeModel(grade.getGrade(), course);

        synchronized (students) {
            students.addGrade(gradeObject, student);
        }

        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(gradeObject.getGradeID())).build();
        return Response.created(uri).entity(gradeObject).build();
    }

    @Path("/{index}")
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudent(@PathParam("index") final Long studentIndex) {
        StudentModel student;
        synchronized (students) {
            student = students.getStudentByIndex(studentIndex);
        }

        if (student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        }

        return Response.ok(student).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createStudent(@NotNull @Valid StudentModel student, @Context UriInfo uriInfo) {
        StudentModel newStudent;

        synchronized(students) {
            newStudent = students.addStudent(student);
        }

        if(newStudent==null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Already exists").build());
        } else {
            URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(newStudent.getIndex())).build();
            return Response.created(uri).entity(student).build();
        }
    }

    @Path("/{index}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateStudent(@PathParam("index") final long studentIndex, StudentModel updatedStudent) {
        StudentModel student;

        synchronized(students) {
            student = students.getStudentByIndex(studentIndex);

            if(student != null) {
                if(updatedStudent.getName() != null) student.setName(updatedStudent.getName());
                if(updatedStudent.getSurname() != null) student.setSurname(updatedStudent.getSurname());
                if(updatedStudent.getBirth() != null) student.setBirth(updatedStudent.getBirth());
            }
        }

        if(student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        }

        return Response.ok(student).build();
    }

    @Path("/{index}")
    @DELETE
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteStudent(@PathParam("index") final long studentIndex) {
        StudentModel student;

        synchronized(students) {
            student = students.getStudentByIndex(studentIndex);

            if(student != null) {
                students.removeStudent(student);
            }
        }

        if(student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        } else {
            return Response.ok(student).build();
        }
    }
}
