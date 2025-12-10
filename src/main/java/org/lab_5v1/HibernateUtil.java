package org.lab_5v1;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.lab_5v1.cpu_lib.instructions.Instructions;

public class HibernateUtil {
    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.url",
                    "jdbc:sqlite:C:\\Users\\felyl\\DB\\instructions_table.db");
            configuration.configure();

            configuration.addAnnotatedClass(Instructions.class);

            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()

                    .applySettings(configuration.getProperties())

                    .build();

            sessionFactory = configuration.buildSessionFactory(registry);
        }

        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}