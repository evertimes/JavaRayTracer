package com.evertimes.datatype;

public class Light {
    public int type;
    public double intensity;
    public Vector dirpos;

    public Light(int type, double intensity, Vector dirpos) {
        this.type = type;
        this.intensity = intensity;
        this.dirpos = dirpos;
    }
}
