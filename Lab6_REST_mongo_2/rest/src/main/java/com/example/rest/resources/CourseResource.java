package com.example.rest.resources;

import com.example.rest.models.Course;
import com.example.rest.models.Student;
import com.example.rest.mongo.DatastoreUtil;
import com.example.rest.mongo.Utils;
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
import java.util.ArrayList;
import java.util.Objects;


@Path("/courses")
public class CourseResource {
    public CourseResource() {}

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourses(@QueryParam("courseInstructor") String courseInstructor, @QueryParam("name") String name) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        Query<Course> query = datastore.createQuery(Course.class);

        if(courseInstructor != null ) {
            query.field("courseInstructor").containsIgnoreCase(courseInstructor);
        }

        if(name != null ) {
            query.field("name").containsIgnoreCase(name);
        }

        return Response.ok(query.asList()).build();
    }

    @Path("/{courseID}")
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourse(@PathParam("courseID") final String courseID) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        Course course = null;
        ArrayList<Course> courses = (ArrayList<Course>) datastore.find(Course.class).asList();
        for(Course c : courses) {
            if(Objects.equals(c.getCourseID(), courseID)) course = c;
        }
        //Course course = datastore.find(Course.class).field("courseID").equal(courseID).get();

        if(course == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        }

        return Response.ok(course).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCourse(@NotNull @Valid Course course, @Context UriInfo uriInfo) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        Course newCourse = new Course(course.getName(), course.getCourseInstructor());
        //do {
        //    newCourse = new Course(course.getName(), course.getCourseInstructor());
        //}while(datastore.exists(newCourse.getCourseID())!= null);


        datastore.save(newCourse);

        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(newCourse.getCourseID())).build();
        return Response.created(uri).entity(newCourse).build();
    }

    @Path("/{courseID}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam("courseID") final String courseID, Course updatedCourse) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        //Course course = datastore.find(Course.class).field("courseID").equal(courseID).get();
        Course course = null;
        ArrayList<Course> courses = (ArrayList<Course>) datastore.find(Course.class).asList();
        for(Course c : courses) {
            if(Objects.equals(c.getCourseID(), courseID)) course = c;
        }

        if(course == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        } else {
            if(updatedCourse.getName() != null) course.setName(updatedCourse.getName());
            if(updatedCourse.getCourseInstructor() != null) course.setCourseInstructor(updatedCourse.getCourseInstructor());
        }

        datastore.save(course);

        return Response.ok(course).build();
    }

    @Path("/{courseID}")
    @DELETE
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteCourse(@PathParam("courseID") final String courseID) {
        Datastore datastore = DatastoreUtil.getInstance().getDatastore();

        //Course course = datastore.find(Course.class).field("courseID").equal(courseID).get();
        Course course = null;
        ArrayList<Course> courses = (ArrayList<Course>) datastore.find(Course.class).asList();
        for(Course c : courses) {
            if(Objects.equals(c.getCourseID(), courseID)) course = c;
        }

        ArrayList<Student> students = (ArrayList<Student>) datastore.find(Student.class).asList();

        if(course == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        }

        for (Student student : students) {
            Utils.removeCourseGrades(student, course.getCourseID());

            datastore.save(student);
        }

        datastore.delete(course);

        return Response.ok(course).build();
    }
}
