package com.test.server.foobars;

import javax.persistence.*;

/**
 * Todo describe class
 */
@Entity
@Table( name = "FooBar" )
public class FooBar {


    private int id;
    private String name;


    public FooBar(String name) {
        this.name = name;
    }


    protected FooBar() {
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "LastName")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return "FooBar{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
