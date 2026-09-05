package rw.ac.auca.drivingschool.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A qualified driving instructor employed by the school.
 *
 * @author  Student Name
 * @version 1.0
 */
@Entity
@Table(name = "instructor")
public class Instructor extends Audit {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "instructor_id", length = 6)
    private String instructorId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    /** Highest licence category this instructor may teach. */
    @Column(name = "license_category", nullable = false, length = 1)
    private String licenseCategory;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    public Instructor() {
        this.hireDate = LocalDate.now();
        this.licenseCategory = "B";
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
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

    public String getLicenseCategory() {
        return licenseCategory;
    }

    public void setLicenseCategory(String licenseCategory) {
        this.licenseCategory = licenseCategory;
    }

    /**
     * Compares by primary key. See Student.equals() for why JSF needs this.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Instructor)) {
            return false;
        }
        Instructor that = (Instructor) other;
        return instructorId != null && instructorId.equals(that.instructorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instructorId);
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
}
