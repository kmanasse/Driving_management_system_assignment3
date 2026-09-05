package rw.ac.auca.drivingschool.bean;

import rw.ac.auca.drivingschool.dao.StudentDao;
import rw.ac.auca.drivingschool.model.Student;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Backing bean for the student CRUD screen.
 *
 * @ViewScoped means one instance lives for as long as the user stays on the
 * same page, so the table and the form being edited survive a postback. The
 * bean must be Serializable because the server may write view state to disk.
 *
 * @author  Student Name
 * @version 1.0
 */
@Named
@ViewScoped
public class StudentBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final StudentDao studentDao = new StudentDao();

    private List<Student> students = new ArrayList<>();
    private Student student = new Student();
    private boolean editing;

    @PostConstruct
    public void init() {
        reload();
    }

    private void reload() {
        this.students = studentDao.findAll();
    }

    /**
     * CREATE and UPDATE share one button. Which one runs depends on whether
     * the form was opened by "New" or by "Edit".
     *
     * VALIDATION TYPE 3 of 3 - application level validation.
     * The uniqueness check below cannot be done by a tag or by a custom
     * validator, because answering "is this ID already taken" requires a
     * database query. Rules that need to look at other rows belong here, in
     * the bean, after JSF has finished its own validation phase.
     */
    public void save() {
        try {
            if (editing) {
                studentDao.update(student);
                addMessage(FacesMessage.SEVERITY_INFO, "Student updated",
                        student.getFullName() + " was saved.");
            } else {
                if (studentDao.existsById(student.getStudentId())) {
                    addMessage(FacesMessage.SEVERITY_ERROR, "Duplicate student ID",
                            "A student with ID " + student.getStudentId()
                                    + " is already registered. Choose a different ID.");
                    return;
                }
                studentDao.save(student);
                addMessage(FacesMessage.SEVERITY_INFO, "Student registered",
                        student.getFullName() + " was enrolled successfully.");
            }
            clear();
            reload();
        } catch (RuntimeException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Could not save student", ex.getMessage());
        }
    }

    /** Loads the selected row into the form. */
    public void edit(Student selected) {
        this.student = studentDao.findById(selected.getStudentId());
        this.editing = true;
    }

    /** DELETE. */
    public void delete(Student selected) {
        try {
            studentDao.delete(selected.getStudentId());
            addMessage(FacesMessage.SEVERITY_INFO, "Student deleted",
                    selected.getFullName() + " and their lessons were removed.");
            clear();
            reload();
        } catch (RuntimeException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Could not delete student", ex.getMessage());
        }
    }

    /** Resets the form back to a blank new student. */
    public void clear() {
        this.student = new Student();
        this.editing = false;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, summary, detail));
    }

    /** Used by the table to colour each row by status. */
    public String statusStyleClass(String status) {
        if (status == null) {
            return "";
        }
        return "status-" + status.toLowerCase();
    }

    public String[] getLicenseCategories() {
        return new String[]{"A", "B", "C"};
    }

    public String[] getStatuses() {
        return new String[]{"ACTIVE", "COMPLETED", "SUSPENDED"};
    }

    public List<Student> getStudents() {
        return students;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public boolean isEditing() {
        return editing;
    }
}
