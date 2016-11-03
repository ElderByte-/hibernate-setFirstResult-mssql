package com.test.server;

import org.hibernate.SessionFactory;
//import org.hibernate.boot.MetadataSources;
//import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import org.hibernate.cfg.Configuration;

/**
 * Manages the session factory.
 */
public class FactoryManager {

    private SessionFactory sessionFactory;


    public SessionFactory getSessionFactory(){

        if(this.sessionFactory == null){

            setUpFactory();

            System.out.println("Initialized Session factory for hibernate version " + getHibernateVersion());
        }

        return this.sessionFactory;
    }


    private String getHibernateVersion(){
        return org.hibernate.Version.getVersionString();
    }


    /**
     * Set up method for hibernate 4. Must be commented out if using hibernate 5
     */
    private void setUpFactory() {
        Configuration configuration = new Configuration().configure();
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties());
        this.sessionFactory = configuration.buildSessionFactory(ssrb.build());
    }


    /**
     * Set up method for hibernate 5. Must be commented out if using hibernate 4
     */
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
