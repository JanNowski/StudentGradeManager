package rest.resources;

import rest.models.GradeModel;
import rest.models.StudentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Maks on 27.04.2017.
 */
public final class Students {
    private static ArrayList<StudentModel> students = new ArrayList<>();
    private SimpleDateFormat dateFormat;

    public Students() {
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    public ArrayList<StudentModel> getStudents() {
        return students;
    }

    public StudentModel getStudentByIndex(long index) {
        StudentModel student = null;
        for(StudentModel s : students) {
            if(s.getIndex()==index) student = s;
        }

        return student;
    }

    public StudentModel addStudent(StudentModel student) {
        StudentModel newStudent;
        do {
            newStudent = new StudentModel(student.getName(), student.getSurname(), student.getBirth());
        }while(getStudentByIndex(newStudent.getIndex())!= null);

        students.add(newStudent);
        return newStudent;
    }

    public void removeStudent(StudentModel student) {
        students.remove(student);
    }

    public void addGrade(GradeModel grade, StudentModel student) {
        ArrayList<GradeModel> grades = student.getGrades();

        grades.add(grade);
    }

    public GradeModel getGrade(Long index, long id) {
        GradeModel grade = null;
        for(StudentModel s : students) {
            if(s.getIndex()==index){
                for(GradeModel g : s.getGrades()) {
                    if(/*Objects.equals(String.valueOf*/(g.getGradeID()== id)) grade = g;
                }
            }
        }

        return grade;
    }

    public GradeModel getGrade(long id) {
        GradeModel grade = null;
        for(StudentModel s : students) {
            for(GradeModel g : s.getGrades()) {
                if(/*Objects.equals(String.valueOf*/(g.getGradeID()== id)) grade = g;
            }
        }

        return grade;
    }

    public void removeGrade(Long index, long id) {
        getStudentByIndex(index).getGrades().remove(getGrade(index, id));
    }

    public void removeGrade(long id) {
        StudentModel student = null;
        GradeModel grade = null;
        for(StudentModel s : students) {
            for(GradeModel g : s.getGrades()) {
                if(g.getGradeID()==id) {
                    grade = g;
                    student = s;
                }
            }
        }

        if(student != null) student.getGrades().remove(grade);
    }

    public void removeCourseGrades(long courseID) {
        ArrayList<Object[]> grades = new ArrayList<>();

        for(StudentModel s : students) {
            for(GradeModel g : s.getGrades()) {
                if(g.getCourse().getCourseID()==courseID) grades.add(new Object[]{s.getIndex(), g.getGradeID()});
            }
        }

        for(Object[] gradeToRemove : grades) {
            removeGrade((Long) gradeToRemove[0], (Long) gradeToRemove[1]);
        }
    }
}
