package com.example.rest.models;

import com.example.rest.IndexGenerator;
import com.example.rest.resources.GradeResource;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.mongodb.morphia.annotations.Reference;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;


@XmlRootElement
public class Grade {
    @NotNull
    private String gradeID;

    @NotNull
    private Double grade;

    @NotNull
    @JsonFormat(shape=JsonFormat.Shape.STRING,
            pattern="yyyy-MM-dd", timezone="CET")
    private Date date;

    @Reference
    @NotNull
    private Course course;

    @InjectLink(
        resource = GradeResource.class,
        style = InjectLink.Style.ABSOLUTE,
        method = "getGrade",
        bindings = @Binding(name = "gradeID", value = "${instance.gradeID}"),
        rel = "self"
    )
    @XmlElement(name = "link")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private Link link;

    public Grade() {
        this.gradeID = IndexGenerator.createRandomIndex();
    }

    public Grade(Double grade, Course course) {
        this.gradeID = IndexGenerator.createRandomIndex();
        this.grade = grade;
        this.date = new Date();
        this.course = course;
    }

    public Grade(Double grade, Course course, Date date) {
        this.gradeID = IndexGenerator.createRandomIndex();
        this.grade = grade;
        this.date = date;
        this.course = course;
    }

    public String getGradeID() {
        return gradeID;
    }

    public Double getGrade() {
        return grade;
    }

    public Date getDate() {
        return date;
    }

    public Course getCourse() {
        return course;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
