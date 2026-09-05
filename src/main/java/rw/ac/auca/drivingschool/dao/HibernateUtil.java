package rw.ac.auca.drivingschool.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Holds the single application-wide SessionFactory.
 *
 * A SessionFactory is expensive: building one parses hibernate.cfg.xml, scans
 * every mapped entity, builds metadata and opens a JDBC connection pool. It is
 * also thread safe, so an application needs exactly one for its entire
 * lifetime. Building a new one per DAO call would leak connection pools until
 * the database refused new connections.
 *
 * The static initialiser below runs once, the first time any code touches this
 * class, and the result is reused forever after.
 *
 * @author  Student Name
 * @version 1.0
 */
public final class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (SESSION_FACTORY != null && !SESSION_FACTORY.isClosed()) {
            SESSION_FACTORY.close();
        }
    }
}
