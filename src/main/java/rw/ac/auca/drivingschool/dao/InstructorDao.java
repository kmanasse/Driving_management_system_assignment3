package rw.ac.auca.drivingschool.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import rw.ac.auca.drivingschool.model.Instructor;

import java.util.List;

/**
 * Data access for Instructor. Instructors are supporting data for the lesson
 * scheduler, so only create and read are needed in this phase.
 *
 * @author  Student Name
 * @version 1.0
 */
public class InstructorDao {

    public void save(Instructor instructor) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(instructor);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }

    public List<Instructor> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("SELECT i FROM Instructor i ORDER BY i.firstName", Instructor.class)
                    .list();
        }
    }

    public Instructor findById(String instructorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Instructor.class, instructorId);
        }
    }
}
