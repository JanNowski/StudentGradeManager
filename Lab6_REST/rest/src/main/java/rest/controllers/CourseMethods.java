package rest.controllers;

import rest.models.CourseModel;
import rest.resources.Courses;
import rest.resources.Students;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by Maks on 27.04.2017.
 */
@Path("/courses")
public class CourseMethods {
    private final Courses courses;
    private final Students students;

    public CourseMethods() {
        this.courses = new Courses();
        this.students = new Students();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public ArrayList<CourseModel> getCourses() {
        ArrayList<CourseModel> array;
        synchronized(courses) {
            array = courses.getCourses();
            return array;
        }

        //return Response.ok(array).build();
    }

    @Path("/{courseID}")
    @GET
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCourse(@PathParam("courseID") final long courseID) {
        CourseModel course;
        synchronized(courses) {
            course = courses.getCourse(courseID);
        }

        if(course == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        }

        return Response.ok(course).build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCourse(@NotNull @Valid CourseModel course, @Context UriInfo uriInfo) {
        synchronized(courses) {
            courses.addCourse(course);
        }

        URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(course.getCourseID())).build();
        return Response.created(uri).entity(course).build();
    }

    @Path("/{courseID}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateCourse(@PathParam("courseID") final long courseId, CourseModel updatedCourse) {
        CourseModel course;

        synchronized(courses) {
            course = courses.getCourse(courseId);

            if(course != null) {
                if(updatedCourse.getName() != null) course.setName(updatedCourse.getName());
                if(updatedCourse.getCourseInstructor() != null) course.setCourseInstructor(updatedCourse.getCourseInstructor());
            }
        }

        if(course == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        }

        return Response.ok(course).build();
    }

    @Path("/{courseID}")
    @DELETE
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response deleteCourse(@PathParam("courseID") final long courseId) {
        CourseModel course;

        synchronized(courses) {
            synchronized (students) {
                course = courses.getCourse(courseId);

                if (course != null) {
                    courses.removeCourse(course);
                }

                students.removeCourseGrades(courseId);
            }
        }

        if(course == null) {
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).entity("Nie znaleziono").build());
        } else {
            return Response.ok(course).build();
        }
    }
}
