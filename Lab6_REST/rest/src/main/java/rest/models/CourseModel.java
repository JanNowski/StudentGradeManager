package rest.models;

import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import rest.controllers.CourseMethods;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Maks on 27.04.2017.
 */
@XmlRootElement
public class CourseModel {
    private final static AtomicLong idCounter = new AtomicLong();

    @NotNull
    private long courseID;

    @NotNull
    private String name;

    @NotNull
    private String courseInstructor;

    @InjectLinks({
        @InjectLink(
            resource = CourseMethods.class,
            style = InjectLink.Style.ABSOLUTE,
            rel = "all_courses"
        ),
        @InjectLink(
                resource = CourseMethods.class,
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

    public CourseModel() {
        this.courseID = idCounter.incrementAndGet();
    }

    public CourseModel(String name, String courseInstructor) {
        this.courseID = idCounter.incrementAndGet();
        this.name = name;
        this.courseInstructor = courseInstructor;
    }

    public long getCourseID() {
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
