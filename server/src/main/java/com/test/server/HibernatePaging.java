package com.test.server;

import com.test.server.foobars.FooBarService;
import org.hibernate.SessionFactory;

/**
 * Todo describe class
 */
public class HibernatePaging {


    public static void main(String[] args){

        System.out.println("Started Hibernate Paging Test app");
        FactoryManager factoryManager = new FactoryManager();


        SessionFactory sessionFactory = factoryManager.getSessionFactory();
        FooBarService fooBarService = new FooBarService(sessionFactory);



        fooBarService.startTest();


    }




}
