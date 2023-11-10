package org.example;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

class HibernateUtil {
    private static SessionFactory sessionFactory = null;

    static {
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            sessionFactory = new MetadataSources(registry)
                    .addAnnotatedClass(Task.class)
                    .buildMetadata()
                    .buildSessionFactory();
//            Configuration configuration = new Configuration().configure();
//            sessionFactory = configuration.addAnnotatedClass(Task.class).buildSessionFactory();
        } catch (Throwable e) {
            System.err.println("Session factory error");
            e.printStackTrace();

            System.exit(1);
        }
    }

    static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}