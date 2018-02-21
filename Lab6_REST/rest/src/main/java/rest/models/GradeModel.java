package rest.models;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import rest.controllers.GradeMethods;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Maks on 27.04.2017.
 */

@XmlRootElement
public class GradeModel {
    @NotNull
    private long gradeID;

    @NotNull
    private Double grade;

    @NotNull
    private String date;

    @NotNull
    private CourseModel course;
    private final static AtomicLong idCounter = new AtomicLong();

    @InjectLink(
        resource = GradeMethods.class,
        style = InjectLink.Style.ABSOLUTE,
        method = "getGrade",
        bindings = @Binding(name = "gradeID", value = "${instance.gradeID}"),
        rel = "self"
    )
    @XmlElement(name = "link")
    @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
    private Link link;

    public GradeModel() {
        this.gradeID = idCounter.incrementAndGet();
    }

    public GradeModel(Double grade, CourseModel course) {
        this.gradeID = idCounter.incrementAndGet();
        this.grade = grade;
        this.date = new Date().toString();
        this.course = course;
    }

    public long getGradeID() {
        return gradeID;
    }

    public Double getGrade() {
        return grade;
    }

    public String getDate() {
        return date;
    }

    public CourseModel getCourse() {
        return course;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCourse(CourseModel course) {
        this.course = course;
    }
}
