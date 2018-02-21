package com.example.rest.resources;

import com.example.rest.models.Course;
import com.example.rest.models.Grade;
import com.example.rest.models.Student;
import com.example.rest.mongo.DatastoreUtil;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;



@Path("/students")
public class StudentResource {
    private final ArrayList<Double> gradeScale = new ArrayList<>(Arrays.asList(2.0, 3.0, 3.5, 4.0, 4.5, 5.0));

    public StudentResource() {}

    private Boolean checkGrade(Double gradeToCheck) {
        Boolean isOneOfGrades = false;

        for(Double grade : gradeScale) {
            if(Objects.equals(grade, gradeToCheck)) isOneOfGrades = true;
        }

        return isOneOfGrades;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudents(@QueryParam("name") String name, @QueryParam("surname") String surname,
                                @QueryParam("birth") Date birth, @QueryParam("range") String range) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        Query<Student> query = datastore.createQuery(Student.class);

        if(name != null ) {
            query.field("name").containsIgnoreCase(name);
        }

        if(surname != null ) {
            query.field("surname").containsIgnoreCase(surname);
        }

        if (birth != null) {
            if(Objects.equals(range, "before")) {
                query.filter("birth <", birth);
            } else if (Objects.equals(range, "after")) {
                query.filter("birth >", birth);
            }
            else {
                query.filter("birth ==", birth);
            }
        }

        return Response.ok(query.asList()).build();
    }

    @Path("/{index}/grades")
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudentGrades(@PathParam("index") final Long studentIndex, @QueryParam("courseID") String courseID, @QueryParam("grade") double grade, @QueryParam("course") String course, @QueryParam("range") String range) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        Student student = datastore.find(Student.class).field("index").equal(studentIndex).get();

        if(student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        }

        List<Grade> grades = student.getGrades();

        if(courseID != null) {
            grades = grades.stream().filter(_grade -> Objects.equals(_grade.getCourse().getCourseID(), courseID)).collect(Collectors.toList());
        }

        if(course != null ) {
            grades = grades.stream().filter(_grade -> _grade.getCourse().getName().toLowerCase().contains(course.toLowerCase())).collect(Collectors.toList());
        }

        if (grade >= 2 && grade <= 5) {
            if(Objects.equals(range, "higher")) {
                grades = grades.stream().filter(_grade -> _grade.getGrade() > grade).collect(Collectors.toList());
            } else if (Objects.equals(range, "lower")) {
                grades = grades.stream().filter(_grade -> _grade.getGrade() < grade).collect(Collectors.toList());
            }
            else {
                grades = grades.stream().filter(_grade -> _grade.getGrade() == grade).collect(Collectors.toList());
            }
        }

        return Response.ok(grades).build();
    }

    @Path("/{index}/grades")
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createGrade(@PathParam("index") final Long studentIndex, final Grade grade, @Context UriInfo uriInfo) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        Student student = datastore.find(Student.class).field("index").equal(studentIndex).get();
        //ArrayList<Student> students = (ArrayList<Student>) datastore.find(Student.class).asList();

        if(student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Student nie istnieje").build());
        }


        Course course = null;
        ArrayList<Course> courses = (ArrayList<Course>) datastore.find(Course.class).asList();
        for(Course c : courses) {
            if(Objects.equals(c.getCourseID(), grade.getCourse().getCourseID())) course = c;
        }
        //Course course = datastore.find(Course.class).field("courseID").equal(grade.getCourse().getCourseID()).get();

        if(course == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Przedmiot nie istnieje").build());
        }

        if(!checkGrade(grade.getGrade())) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Ocena jest niepoprawna").build());
        }

        Grade gradeObject = new Grade(grade.getGrade(), course);//, grade.getDate());

        student.getGrades().add(gradeObject);

        datastore.save(student);

        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(gradeObject.getGradeID())).build();
        return Response.created(uri).entity(gradeObject).build();
    }

    @Path("/{index}")
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudent(@PathParam("index") final Long studentIndex) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        Student student = datastore.find(Student.class).field("index").equal(studentIndex).get();

        if (student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        }

        return Response.ok(student).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createStudent(@NotNull @Valid Student student, @Context UriInfo uriInfo) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        student.setIndex((long) CounterResource.instance.getSeq("students"));
        datastore.save(student);

        URI uri = uriInfo.getAbsolutePathBuilder().path(Long.toString(student.getIndex())).build();
        return Response.created(uri).entity(student).build();
    }

    @Path("/{index}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateStudent(@PathParam("index") final long studentIndex, Student updatedStudent) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        Student student = datastore.find(Student.class).field("index").equal(studentIndex).get();

        if(student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        }

        if (updatedStudent.getName() != null) student.setName(updatedStudent.getName());
        if (updatedStudent.getSurname() != null) student.setSurname(updatedStudent.getSurname());
        if (updatedStudent.getBirth() != null) student.setBirth(updatedStudent.getBirth());

        datastore.save(student);

        return Response.ok(student).build();
    }

    @Path("/{index}")
    @DELETE
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteStudent(@PathParam("index") final long studentIndex) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        Student student = datastore.find(Student.class).field("index").equal(studentIndex).get();

        if(student == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        }

        datastore.delete(student);

        return Response.ok(student).build();
    }
}
