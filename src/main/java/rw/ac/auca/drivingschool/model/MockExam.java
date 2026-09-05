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

/**
 * A theory mock exam sat by a student before the national test.
 *
 * Modelled in the class diagram and mapped by Hibernate, but not given a CRUD
 * screen in this phase of the project.
 *
 * @author  Student Name
 * @version 1.0
 */
@Entity
@Table(name = "mock_exam")
public class MockExam extends Audit {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Long examId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "passed", nullable = false)
    private boolean passed;

    public MockExam() {
        this.examDate = LocalDate.now();
    }

    public Long getExamId() {
        return examId;
    }

    public void setExamId(Long examId) {
        this.examId = examId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }
}
