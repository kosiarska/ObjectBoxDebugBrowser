package com.sample;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by Michał Trętowicz
 */
@Entity
public class Person {

    @Id
    private long id;

    String name;

    public Person(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
