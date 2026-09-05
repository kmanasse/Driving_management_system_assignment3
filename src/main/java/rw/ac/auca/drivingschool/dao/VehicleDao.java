package rw.ac.auca.drivingschool.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import rw.ac.auca.drivingschool.model.Vehicle;

import java.util.List;

/**
 * Data access for Vehicle. Vehicles are supporting data for the lesson
 * scheduler, so only create and read are needed in this phase.
 *
 * @author  Student Name
 * @version 1.0
 */
public class VehicleDao {

    public void save(Vehicle vehicle) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(vehicle);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }

    public List<Vehicle> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("SELECT v FROM Vehicle v ORDER BY v.plateNumber", Vehicle.class)
                    .list();
        }
    }

    public Vehicle findById(String plateNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Vehicle.class, plateNumber);
        }
    }
}
