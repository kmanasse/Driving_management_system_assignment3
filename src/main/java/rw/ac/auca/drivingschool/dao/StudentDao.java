package rw.ac.auca.drivingschool.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import rw.ac.auca.drivingschool.model.Student;

import java.util.List;

/**
 * Data access for Student. Implements all four CRUD operations.
 *
 * Every method follows the same shape: open a Session, begin a Transaction,
 * do the work, commit, close. The try-with-resources block closes the Session
 * even if an exception is thrown, and the catch block rolls the transaction
 * back so a half-finished write is never left behind.
 *
 * @author  Student Name
 * @version 1.0
 */
public class StudentDao {

    /** CREATE. */
    public void save(Student student) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(student);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }

    /** READ all, newest enrolment first. */
    public List<Student> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("SELECT s FROM Student s ORDER BY s.studentId", Student.class)
                    .list();
        }
    }

    /** READ one by primary key. */
    public Student findById(String studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Student.class, studentId);
        }
    }

    /**
     * UPDATE. merge() is used instead of update() because the object coming
     * back from a JSF form is detached: it was loaded in an earlier Session
     * that has since been closed. merge() copies its state onto a managed
     * instance instead of complaining that the object is already known.
     */
    public void update(Student student) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(student);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }

    /**
     * DELETE.
     *
     * A student cannot simply be deleted while lesson, payment or mock exam
     * rows still point at them: PostgreSQL enforces the foreign keys and would
     * reject the statement. So the dependent rows are removed first, inside
     * the same transaction, using bulk HQL deletes.
     *
     * Bulk delete is used rather than loading each child object because it
     * issues one DELETE statement per table instead of one per row, and
     * nothing here needs the child objects in memory.
     */
    public void delete(String studentId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            session.createQuery("DELETE FROM Lesson l WHERE l.student.studentId = :id")
                    .setParameter("id", studentId)
                    .executeUpdate();

            session.createQuery("DELETE FROM Payment p WHERE p.student.studentId = :id")
                    .setParameter("id", studentId)
                    .executeUpdate();

            session.createQuery("DELETE FROM MockExam m WHERE m.student.studentId = :id")
                    .setParameter("id", studentId)
                    .executeUpdate();

            Student managed = session.get(Student.class, studentId);
            if (managed != null) {
                session.delete(managed);
            }

            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }

    /** Used by the uniqueness check when registering a new student. */
    public boolean existsById(String studentId) {
        return findById(studentId) != null;
    }
}
