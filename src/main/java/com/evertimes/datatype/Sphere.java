package com.evertimes.datatype;

public class Sphere {
    public Vector center;
    public double radius;
    public int color;
    public int specular;
    public double reflective;

    public Sphere(Vector center, double radius, int color, int specular, double reflective) {
        this.center = center;
        this.radius = radius;
        this.color = color;
        this.specular = specular;
        this.reflective = reflective;
    }
}
