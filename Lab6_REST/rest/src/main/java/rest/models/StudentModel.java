package rest.models;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import rest.IndexGenerator;
import rest.controllers.StudentMethods;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maks on 27.04.2017
 */

@XmlRootElement
public class StudentModel {
    //public final static AtomicLong idCounter = new AtomicLong();

    @DecimalMin(value = "1")
    private Long index;

    @NotNull
    private String name;

    @NotNull
    private String surname;

    @NotNull
    private String birth;

    private ArrayList<GradeModel> grades = new ArrayList<>();

    @InjectLinks({
        @InjectLink(
            resource = StudentMethods.class,
            style = InjectLink.Style.ABSOLUTE,
            method = "getStudents",
            rel = "all_students"
        ),
        @InjectLink(
            resource = StudentMethods.class,
            style = InjectLink.Style.ABSOLUTE,
            method = "getStudent",
            bindings = @Binding(name = "index", value = "${instance.index}"),
            rel = "self"
        ),
        @InjectLink(
            resource = StudentMethods.class,
            method = "getStudentGrades",
            bindings = @Binding(name = "index", value = "${instance.index}"),
            style = InjectLink.Style.ABSOLUTE,
            rel = "grades"
        )
    })
    @XmlElement(name = "link")
    @XmlElementWrapper(name = "links")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private List<Link> link;

    public StudentModel() {}

    public StudentModel(String name, String surname, String birth) {
        this.index = IndexGenerator.createRandomIndex();
        this.name = name;
        this.surname = surname;
        this.birth = birth;
    }

    public Long getIndex() {
        return index;
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

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public ArrayList<GradeModel> getGrades() {
        return grades;
    }

    public void setGrades(ArrayList<GradeModel> grades) {
        this.grades = grades;
    }
}
