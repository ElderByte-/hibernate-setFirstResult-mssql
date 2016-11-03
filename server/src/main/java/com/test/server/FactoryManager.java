package com.test.server;

import org.hibernate.SessionFactory;
//import org.hibernate.boot.MetadataSources;
//import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

/**
 * Todo describe class
 */
public class FactoryManager {


    private SessionFactory sessionFactory;


    public SessionFactory getSessionFactory(){

        if(this.sessionFactory == null){
            setUpFactory();
        }

        return this.sessionFactory;
    }





    private void setUpFactory() {
        // A SessionFactory is set up once for an application!

        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties());
        this.sessionFactory = configuration.buildSessionFactory(ssrb.build());
    }


    /*
    private void setUpFactory() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            this.sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy( registry );
            e.printStackTrace();
        }
    }
    */


}
