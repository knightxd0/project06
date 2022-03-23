package com.linebot.project6.OOP;
public class female extends health {
    // field

    // construct
    public female(double w, double h, int a) {
        super(w, h, a, "female");
    }

    // method
    public double BMI() {
        return getWeight() / (getHeight() * getHeight());
    }

    public double Calories() {
        return getWeight() * 27.0;
    }

    @Override
    public void Display() {

        System.out.println("weight: " + getWeight());
        System.out.println("height: " + getHeight());
        System.out.println("age: " + getAge());
        System.out.println("gender: " + getGender());

        System.out.printf("BMI: %.2f\n", BMI());
        System.out.println("St: " + Standard(BMI()));
        System.out.println("Calories: " + Calories());
    }
}
