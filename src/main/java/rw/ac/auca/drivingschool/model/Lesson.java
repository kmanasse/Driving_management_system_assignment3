package rw.ac.auca.drivingschool.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * A single practical driving lesson: one student, one instructor and one
 * vehicle occupying one time slot on one date.
 *
 * This is the entity the whole scheduling problem revolves around. Because a
 * lesson holds three foreign keys, the same instructor or the same vehicle can
 * accidentally be booked into two overlapping lessons. Preventing that is the
 * job of LessonDao.findConflicts() and LessonBean.save().
 *
 * All three @ManyToOne associations are EAGER on purpose. The DAO closes its
 * Hibernate Session before returning, so a LAZY association would throw
 * LazyInitializationException the moment a JSF page tried to render
 * lesson.student.fullName.
 *
 * @author  Student Name
 * @version 1.0
 */
@Entity
@Table(name = "lesson")
public class Lesson extends Audit {

    private static final long serialVersionUID = 1L;

    /**
     * Unlike Student, this key is generated. Nobody wants to invent an
     * identifier every time they book a lesson.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Long lessonId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plate_number", nullable = false)
    private Vehicle vehicle;

    @Column(name = "lesson_date", nullable = false)
    private LocalDate lessonDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    /** SCHEDULED, COMPLETED, CANCELLED or ABSENT. */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "notes", length = 255)
    private String notes;

    public Lesson() {
        this.lessonDate = LocalDate.now();
        this.startTime = LocalTime.of(8, 0);
        this.endTime = LocalTime.of(9, 0);
        this.status = "SCHEDULED";
    }

    /** Human readable slot, used in conflict messages and in the table. */
    public String getTimeSlot() {
        return startTime + " - " + endTime;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public void setLessonId(Long lessonId) {
        this.lessonId = lessonId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDate getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(LocalDate lessonDate) {
        this.lessonDate = lessonDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
