package com.test.server;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
//import org.hibernate.query.Query;

import java.util.List;

/**
 * Main entry point and test case.
 */
public class TestCase {


    public static final String hibernateV5 = "5.2.4.Final";
    public static final String hibernateV4 = "4.3.11.Final";

    private final SessionFactory sessionFactory;

    public static void main(String[] args){

        System.out.println("Starting Hibernate Paging Test app...");
        FactoryManager factoryManager = new FactoryManager();
        SessionFactory sessionFactory = factoryManager.getSessionFactory();

        TestCase testCase = new TestCase(sessionFactory);

        testCase.startTest();
    }


    public TestCase(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private int maxResults = 5;

    public void startTest(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        for (int i = 0; i < maxResults; i++) {
            create(session, "James");
        }

        List<FooBar> fooBars = getAllByName(session);

        for(FooBar fooBar : fooBars){
            System.out.println(fooBar.toString());
        }


        session.getTransaction().commit();
        session.close();

    }

    public void create(Session session, String name){
        session.save(new FooBar(name));
    }

    /**
     * Usage of deprecated class Query is intended as we switch between hibernate v4 and 5.
     */
    public List<FooBar> getAllByName(Session session){
        Query query = session.createQuery("from FooBar");
        query.setFirstResult(1); // Hibernate 5 crashes with error "query result offset is not supported"
        query.setMaxResults(maxResults);
        return (List<FooBar>)query.list();
    }





}
