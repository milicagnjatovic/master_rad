package org.example;

import org.example.Tables.*;
import org.example.Views.TaskStatistic;
import org.example.Views.UserStatistic;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private static SessionFactory sessionFactory = null;

    static {
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure()
                    .build();
            sessionFactory = new MetadataSources(registry)
                    .addAnnotatedClass(Grader.class)
                    .addAnnotatedClass(Roles.class)
                    .addAnnotatedClass(SubmissionID.class)
                    .addAnnotatedClass(Submission.class)
                    .addAnnotatedClass(Task.class)
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(TaskStatistic.class)
                    .addAnnotatedClass(UserStatistic.class)
                    .addAnnotatedClass(RoleGraderPermissionID.class)
                    .addAnnotatedClass(RoleGraderPermission.class)
                    .buildMetadata()
                    .buildSessionFactory();

      } catch (Exception e) {
            System.err.println("Session factory error");
            System.err.println(e.getMessage());
            System.err.println(e.getMessage());
            e.printStackTrace();

            System.exit(1);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}