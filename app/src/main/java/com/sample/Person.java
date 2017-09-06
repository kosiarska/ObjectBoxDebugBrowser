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

    float weight;

    double height;

    public Person(long id, String name, float weight, double height) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.height = height;
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

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
