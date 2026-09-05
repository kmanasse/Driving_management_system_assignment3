package rw.ac.auca.drivingschool.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import rw.ac.auca.drivingschool.model.Lesson;

import java.util.List;

/**
 * Data access for Lesson, including the double-booking check that the whole
 * scheduling feature depends on.
 *
 * @author  Student Name
 * @version 1.0
 */
public class LessonDao {

    /** CREATE. */
    public void save(Lesson lesson) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.save(lesson);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }

    /**
     * READ all. The JOIN FETCH clauses matter: without them Hibernate would
     * issue one extra SELECT per lesson to load its student, then another for
     * the instructor, then another for the vehicle. Twenty lessons would cost
     * sixty-one queries instead of one. This is the classic N+1 problem, and
     * JOIN FETCH is the fix.
     */
    public List<Lesson> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT l FROM Lesson l "
                            + "JOIN FETCH l.student "
                            + "JOIN FETCH l.instructor "
                            + "JOIN FETCH l.vehicle "
                            + "ORDER BY l.lessonDate DESC, l.startTime", Lesson.class)
                    .list();
        }
    }

    /** READ one by primary key, with its associations already loaded. */
    public Lesson findById(Long lessonId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT l FROM Lesson l "
                            + "JOIN FETCH l.student "
                            + "JOIN FETCH l.instructor "
                            + "JOIN FETCH l.vehicle "
                            + "WHERE l.lessonId = :id", Lesson.class)
                    .setParameter("id", lessonId)
                    .uniqueResult();
        }
    }

    /** UPDATE. */
    public void update(Lesson lesson) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(lesson);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }

    /** DELETE. */
    public void delete(Long lessonId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Lesson managed = session.get(Lesson.class, lessonId);
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

    /**
     * Returns every already-scheduled lesson that clashes with the one passed
     * in. An empty list means the slot is free.
     *
     * Two lessons clash when ALL of the following hold:
     *
     *   1. They are on the same date.
     *   2. The existing lesson is not cancelled. A cancelled lesson releases
     *      its instructor and vehicle, so it must not block anything.
     *   3. They share at least one resource: the same student, the same
     *      instructor, or the same vehicle. A person or a car can only be in
     *      one place at a time.
     *   4. Their time ranges overlap.
     *
     * Condition 4 is the part students usually get wrong. The test is:
     *
     *      existing.startTime < new.endTime  AND  existing.endTime > new.startTime
     *
     * Work through why. If the existing lesson ends at or before the new one
     * starts, there is no overlap and the second half of the test is false. If
     * the existing lesson starts at or after the new one ends, the first half
     * is false. Every other arrangement — partial overlap at either end, one
     * lesson fully inside the other, identical times — makes both halves true.
     * Strict inequalities are deliberate so that a 09:00-10:00 lesson and a
     * 10:00-11:00 lesson are treated as back-to-back rather than clashing.
     *
     * When editing an existing lesson its own row must be excluded, otherwise
     * every lesson would be reported as clashing with itself.
     */
    public List<Lesson> findConflicts(Lesson candidate) {
        StringBuilder hql = new StringBuilder(
                "SELECT l FROM Lesson l "
                        + "JOIN FETCH l.student "
                        + "JOIN FETCH l.instructor "
                        + "JOIN FETCH l.vehicle "
                        + "WHERE l.lessonDate = :lessonDate "
                        + "AND l.status <> 'CANCELLED' "
                        + "AND ( l.student.studentId = :studentId "
                        + "   OR l.instructor.instructorId = :instructorId "
                        + "   OR l.vehicle.plateNumber = :plateNumber ) "
                        + "AND l.startTime < :endTime "
                        + "AND l.endTime > :startTime");

        boolean editing = candidate.getLessonId() != null;
        if (editing) {
            hql.append(" AND l.lessonId <> :selfId");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Lesson> query = session.createQuery(hql.toString(), Lesson.class);
            query.setParameter("lessonDate", candidate.getLessonDate());
            query.setParameter("studentId", candidate.getStudent().getStudentId());
            query.setParameter("instructorId", candidate.getInstructor().getInstructorId());
            query.setParameter("plateNumber", candidate.getVehicle().getPlateNumber());
            query.setParameter("startTime", candidate.getStartTime());
            query.setParameter("endTime", candidate.getEndTime());
            if (editing) {
                query.setParameter("selfId", candidate.getLessonId());
            }
            return query.list();
        }
    }
}
