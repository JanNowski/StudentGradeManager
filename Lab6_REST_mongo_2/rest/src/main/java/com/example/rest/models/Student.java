package com.example.rest.models;

import com.example.rest.mongo.ObjectIdJaxbAdapter;
import com.example.rest.resources.StudentResource;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity("students")
@XmlRootElement
public class Student {
    @Id
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    private ObjectId id;

    @Indexed(name = "index", unique = true)
    private Long index;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    @JsonFormat(shape=JsonFormat.Shape.STRING,
            pattern="yyyy-MM-dd", timezone="CET")
    private Date birth;

    @Embedded
    private ArrayList<Grade> grades = new ArrayList<>();

    @InjectLinks({
        @InjectLink(
            resource = StudentResource.class,
            style = InjectLink.Style.ABSOLUTE,
            method = "getStudent",
            bindings = @Binding(name = "index", value = "${instance.index}"),
            rel = "self"
        ),
        @InjectLink(
            resource = StudentResource.class,
            method = "getStudentGrades",
            bindings = { @Binding(name = "index", value = "${instance.index}"), @Binding(name = "courseID", value = ""), @Binding(name = "grade", value = ""), @Binding(name = "course", value = ""), @Binding(name = "range", value = "")},
            style = InjectLink.Style.ABSOLUTE,
            rel = "grades"
        )
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> link;

    public Student() {}

    @XmlTransient
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Student(long index, String name, String surname, Date birth) {
        this.index = index;
        this.name = name;
        this.surname = surname;
        this.birth = birth;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public ArrayList<Grade> getGrades() {
        return grades;
    }

    public void setGrades(ArrayList<Grade> grades) {
        this.grades = grades;
    }
}
