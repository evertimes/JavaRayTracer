package com.evertimes.datatype;

public class Vector {
    public double x;
    public double y;
    public double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public static Vector vctrSum(Vector a, Vector b) {
        return new Vector(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vector vctrScale(Vector a, double b){
        return new Vector(a.x * b, a.y * b, a.z * b);
    }

    public static double vctrDot(Vector a, Vector b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Vector vctrSubs(Vector a, Vector b) {
        return new Vector(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static double vctrLen(Vector a) {
        return Math.sqrt(vctrDot(a, a));
    }
}
