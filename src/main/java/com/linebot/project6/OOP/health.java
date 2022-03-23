package com.linebot.project6.OOP;
public abstract class health {
    // field
    private double weight;
    private double height;
    private int age;
    private String gender;
    private String standard;

    // construct
    public health(double w, double h, int a, String gender) {
        setWeight(w);
        setHeight(h);
        setAge(a);
        setGender(gender);
    }

    // abstract method
    public abstract double BMI();

    public abstract double Calories();

    public abstract void Display();

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

    public String getStandard() {
        return this.standard;
    }

    public void setStandard(String st) {
        this.standard = st;
    }

    // method
    public String Standard(double BMI) {
        double b = BMI;
        if (b < 18.50) {
            setStandard("a");
        } else if ((b > 18.59) && (b < 22.90)) {
            setStandard("a");
        } else if ((b > 23.0) && (b < 24.90)) {
            setStandard("a");
        } else if ((b > 25.0) && (b < 29.90)) {
            setStandard("a");
        } else if (b > 30.0) {
            setStandard("a");
        }
        standard = getStandard();
        return standard;
    }

}
