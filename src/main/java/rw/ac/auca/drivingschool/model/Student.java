package rw.ac.auca.drivingschool.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * A person enrolled at the driving school.
 *
 * The primary key is typed in by the registrar (for example "STD001") rather
 * than generated, because driving schools already use their own student
 * numbering on their paper files.
 *
 * @author  Student Name
 * @version 1.0
 */
@Entity
@Table(name = "student")
public class Student extends Audit {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "student_id", length = 6)
    private String studentId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    /** Category of licence the student is training for: A, B or C. */
    @Column(name = "license_category", nullable = false, length = 1)
    private String licenseCategory;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    /** ACTIVE, COMPLETED or SUSPENDED. */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * The inverse side of Lesson.student. mappedBy tells Hibernate that the
     * foreign key lives on the lesson table, so this side owns nothing and
     * creates no extra column.
     *
     * Deliberately declared without a cascade. Deleting a student's lessons is
     * handled explicitly in StudentDao.delete() instead, because cascading a
     * merge through a lazily loaded collection on a detached entity is a
     * well known source of surprising behaviour.
     */
    @OneToMany(mappedBy = "student")
    private List<Lesson> lessons = new ArrayList<>();

    public Student() {
        this.enrollmentDate = LocalDate.now();
        this.status = "ACTIVE";
        this.licenseCategory = "B";
    }

    /** Convenience getter used by the lesson booking drop-down. */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getLicenseCategory() {
        return licenseCategory;
    }

    public void setLicenseCategory(String licenseCategory) {
        this.licenseCategory = licenseCategory;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }
}
