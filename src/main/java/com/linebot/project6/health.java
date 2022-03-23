package com.linebot.project6;

public abstract class health {
    // field
    private double weight;
    private double height;
    private int age;
    private String gender;
    private String standard;

    // construct
    public health() {

    }

    // abstract method
    public abstract double getBMI(double weight, double height);

    public abstract double getCalories();

    public abstract String getStandard(double bmi);

    // get/set
    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return this.height;
    }

    public void setHeight(double height) {
        this.height = height / 100.0;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setStandard(String st) {
        this.standard = st;
    }

    // method

}
