package org.example;

import org.example.Tables.Roles;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

class HibernateUtil {
    private static SessionFactory sessionFactory = null;

    static {
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure()
                    .build();
            sessionFactory = new MetadataSources(registry)
                    .addAnnotatedClass(Roles.class)
                    .buildMetadata()
                    .buildSessionFactory();

      } catch (Throwable e) {
            System.err.println("Session factory error");
            System.err.println(e.getMessage());
            System.err.println(e.getMessage());
            e.printStackTrace();

            System.exit(1);
        }
    }

    static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}