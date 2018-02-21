package com.example.rest.models;

import com.example.rest.IndexGenerator;
import com.example.rest.mongo.ObjectIdJaxbAdapter;
import com.example.rest.resources.CourseResource;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;


@Entity("courses")
@XmlRootElement
public class Course {
    @Id
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    private ObjectId id;

    private String courseID;

    @NotNull
    private String name;

    @NotNull
    private String courseInstructor;

    @InjectLinks({
        @InjectLink(
            resource = CourseResource.class,
            style = InjectLink.Style.ABSOLUTE,
            rel = "all_courses"
        ),
        @InjectLink(
                resource = CourseResource.class,
            style = InjectLink.Style.ABSOLUTE,
            method = "getCourse",
            bindings = @Binding(name = "courseID", value = "${instance.courseID}"),
            rel = "self"
        ),
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> link;

    @XmlTransient
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Course() {
        this.courseID = IndexGenerator.createRandomIndex();
    }

    public Course(String name, String courseInstructor) {
        this.courseID = IndexGenerator.createRandomIndex();
        this.name = name;
        this.courseInstructor = courseInstructor;
    }

    public String getCourseID() {
        return courseID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseInstructor() {
        return courseInstructor;
    }

    public void setCourseInstructor(String courseInstructor) {
        this.courseInstructor = courseInstructor;
    }
}
