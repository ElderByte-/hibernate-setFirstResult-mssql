package com.test.server.foobars;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
//import org.hibernate.query.Query;

import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Todo describe class
 */
public class FooBarService {


    private final SessionFactory sessionFactory;

    public FooBarService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    /*
    public List<FooBar> getAllByName(Session session){
        Query<FooBar> query = session.createQuery("from FooBar", FooBar.class);
        query.setFirstResult(1);
        query.setMaxResults(5);
        return query.list();
    }
    */

    public List<FooBar> getAllByName(Session session){
        Query query = session.createQuery("from FooBar");
        query.setFirstResult(1);
        query.setMaxResults(5);
        return (List<FooBar>)query.list();
    }


    public void create(Session session, String name){
        session.save(new FooBar(name));
    }


    public void startTest(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();


        create(session, "James");
        List<FooBar> fooBars = getAllByName(session);

        for(FooBar fooBar : fooBars){
            System.out.println(fooBar.toString());
        }


        session.getTransaction().commit();
        session.close();

    }




}
