package rw.ac.auca.drivingschool.bean;

import rw.ac.auca.drivingschool.dao.InstructorDao;
import rw.ac.auca.drivingschool.dao.LessonDao;
import rw.ac.auca.drivingschool.dao.StudentDao;
import rw.ac.auca.drivingschool.dao.VehicleDao;
import rw.ac.auca.drivingschool.model.Instructor;
import rw.ac.auca.drivingschool.model.Lesson;
import rw.ac.auca.drivingschool.model.Student;
import rw.ac.auca.drivingschool.model.Vehicle;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Named
@ViewScoped
public class LessonBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LessonDao lessonDao = new LessonDao();
    private final StudentDao studentDao = new StudentDao();
    private final InstructorDao instructorDao = new InstructorDao();
    private final VehicleDao vehicleDao = new VehicleDao();

    private List<Lesson> lessons = new ArrayList<>();
    private List<Student> availableStudents = new ArrayList<>();
    private List<Instructor> availableInstructors = new ArrayList<>();
    private List<Vehicle> availableVehicles = new ArrayList<>();

    private Lesson lesson = new Lesson();
    private boolean editing;

    @PostConstruct
    public void init() {
        reload();
    }

    private void reload() {
        this.lessons = lessonDao.findAll();
        this.availableStudents = studentDao.findAll();
        this.availableInstructors = instructorDao.findAll();
        this.availableVehicles = vehicleDao.findAll();
    }

    /**
     * CREATE and UPDATE, guarded by two application level rules.
     *
     * VALIDATION TYPE 3 of 3 - application level validation.
     *
     * Neither rule below can be expressed with a JSF tag or a custom
     * validator, and the reason is worth stating precisely for the video:
     *
     *   Rule A, end time after start time, is a CROSS FIELD rule. A JSF
     *   validator receives one component and one value. It cannot see the
     *   value of a sibling input, so it can never compare two fields.
     *
     *   Rule B, no double booking, is a CROSS RECORD rule. Answering it means
     *   querying every other lesson already in the database. A validator has
     *   no business asking the database about other rows; that is the job of
     *   the service or bean layer.
     *
     * Both rules therefore run here, after JSF has finished its own conversion
     * and validation phases and populated the Lesson object.
     */
    public void save() {
        // Rule A - cross field check.
        if (lesson.getStartTime() != null && lesson.getEndTime() != null
                && !lesson.getEndTime().isAfter(lesson.getStartTime())) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Invalid time slot",
                    "The end time must be later than the start time.");
            return;
        }

        // Rule B - cross record check against every other scheduled lesson.
        List<Lesson> conflicts = lessonDao.findConflicts(lesson);
        if (!conflicts.isEmpty()) {
            for (Lesson clash : conflicts) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Double booking detected",
                        describeConflict(clash));
            }
            return;
        }

        try {
            if (editing) {
                lessonDao.update(lesson);
                addMessage(FacesMessage.SEVERITY_INFO, "Lesson updated",
                        "The lesson was rescheduled successfully.");
            } else {
                lessonDao.save(lesson);
                addMessage(FacesMessage.SEVERITY_INFO, "Lesson scheduled",
                        lesson.getStudent().getFullName() + " is booked for "
                                + lesson.getLessonDate() + " at " + lesson.getTimeSlot() + ".");
            }
            clear();
            reload();
        } catch (RuntimeException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Could not save lesson", ex.getMessage());
        }
    }

    /**
     * Works out which shared resource caused the clash so the message tells
     * the user something useful instead of just "conflict".
     */
    private String describeConflict(Lesson clash) {
        StringBuilder reason = new StringBuilder();

        if (clash.getInstructor().getInstructorId()
                .equals(lesson.getInstructor().getInstructorId())) {
            reason.append("Instructor ").append(clash.getInstructor().getFullName());
        } else if (clash.getVehicle().getPlateNumber()
                .equals(lesson.getVehicle().getPlateNumber())) {
            reason.append("Vehicle ").append(clash.getVehicle().getPlateNumber());
        } else {
            reason.append("Student ").append(clash.getStudent().getFullName());
        }

        reason.append(" is already booked on ").append(clash.getLessonDate())
                .append(" from ").append(clash.getTimeSlot())
                .append(", which overlaps the slot you requested.");

        return reason.toString();
    }

    public void edit(Lesson selected) {
        this.lesson = lessonDao.findById(selected.getLessonId());
        this.editing = true;
    }

    public void delete(Lesson selected) {
        try {
            lessonDao.delete(selected.getLessonId());
            addMessage(FacesMessage.SEVERITY_INFO, "Lesson deleted",
                    "The lesson was removed from the schedule.");
            clear();
            reload();
        } catch (RuntimeException ex) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Could not delete lesson", ex.getMessage());
        }
    }

    public void clear() {
        this.lesson = new Lesson();
        this.editing = false;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, summary, detail));
    }

    /** Used by the CSS to colour each row by status. */
    public String statusStyleClass(String status) {
        if (status == null) {
            return "";
        }
        return "status-" + status.toLowerCase();
    }

    public String[] getStatuses() {
        return new String[]{"SCHEDULED", "COMPLETED", "CANCELLED", "ABSENT"};
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public List<Student> getAvailableStudents() {
        return availableStudents;
    }

    public List<Instructor> getAvailableInstructors() {
        return availableInstructors;
    }

    public List<Vehicle> getAvailableVehicles() {
        return availableVehicles;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public boolean isEditing() {
        return editing;
    }
}
