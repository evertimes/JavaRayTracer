package com.evertimes;

import com.evertimes.datatype.DoublePair;
import com.evertimes.datatype.Light;
import com.evertimes.datatype.Sphere;
import com.evertimes.datatype.Vector;
import org.jetbrains.skija.Color;

import java.util.List;

import static java.lang.Math.sqrt;
import static com.evertimes.datatype.Vector.*;

public class RayTracer {
    final int sizeX;
    final int sizeY;
    public RayTracer(int width, int height){
        this.sizeX = width;
        this.sizeY = height;
    }
    static final int BACKGROUND_COLOR = Color.makeRGB(0, 0, 0);
    private static final Double step = 0.5;
    int Vh = 1; //Height of view window
    int Vw = 1; //Width of view window
    double dist = 1.0;
    List<Sphere> spheres = List.of(
            new Sphere(new Vector(0.0, -1.0, 3.0), 1.0, 0xFF0000),
            new Sphere(new Vector(2.0, 0.0, 4.0), 1.0, 0x0000FF),
            new Sphere(new Vector(-2.0, 0.0, 4.0), 1.0, 0x00FF00),
            new Sphere(new Vector(0.0, -5001.0, 0.0), 5000.0, 0xFFFFFF));
    List<Light> lights = List.of(
            new Light(0, 0.2, new Vector(0.0, 0.0, 0.0)),
            new Light(1, 0.6, new Vector(2.0, 1.0, 0.0)),
            new Light(2, 0.2, new Vector(1.0, 4.0, 4.0)));
    Vector pointOfView = new Vector(0.0, 0.0, -2.0);

    public void incZView() {
        pointOfView.z+=step;
    }

    public void decZView() {
        pointOfView.z-=step;
    }

    public void incXView() {
        pointOfView.x+=step;
    }

    public void decXView() {
        pointOfView.x-=step;
    }

    public void decYView() {
        pointOfView.y-=step;
    }

    public void incYView() {
        pointOfView.y+=step;
    }

    Vector canvasToViewPort(int x, int y) {
        return new Vector(((double)x)/ sizeX,((double)y)/ sizeY, dist);
    }

    int traceRay(Vector O, Vector D, int t_min, int t_max) {
        var closest_T = Double.valueOf(Integer.MAX_VALUE);
        Sphere closest_sphere = null;
        for (Sphere sphere : spheres) {
            var t = intersectRaySphere(O, D, sphere);
            var debugt = t;
            if (t.getT1() > t_min && t.getT1() < t_max && t.getT1() < closest_T) {
                closest_T = t.getT1();
                closest_sphere = sphere;
            }
            if (t.getT2() > t_min && t.getT2() < t_max && t.getT2() < closest_T) {
                closest_T = t.getT2();
                closest_sphere = sphere;
            }
        }
        if (closest_sphere == null) {
            return BACKGROUND_COLOR;
        }
        var P = vctrSum(O, vctrScale(D, closest_T));
        var N = vctrSubs(P, closest_sphere.center);
        N = vctrScale(N, 1 / vctrLen(N));
        var colorFactor = computeLightning(P, N);
        var red = (Color.getR(closest_sphere.color) * colorFactor);
        if (red > 255)
            red = 255;
        var green = (Color.getG(closest_sphere.color) * colorFactor);
        if (green > 255)
            green = 255;
        var blue = (Color.getB(closest_sphere.color) * colorFactor);
        if (blue > 255)
            blue = 255;
        return Color.makeRGB((int) red, (int) green, (int) blue);
    }

    DoublePair intersectRaySphere(Vector O, Vector D, Sphere sphere) {
        var C = sphere.center;
        var r = sphere.radius;
        var OC = vctrSubs(O, C);
        var k1 = vctrDot(D, D);
        var k2 = 2 * vctrDot(OC, D);
        var k3 = vctrDot(OC, OC) - r * r;

        var discriminant = k2 * k2 - 4 * k1 * k3;
        if (discriminant < 0)
            return new DoublePair(Integer.MAX_VALUE, Integer.MAX_VALUE);
        return new DoublePair(((k2 * (-1) + sqrt(discriminant)) / (2 * k1)), ((k2 * (-1) - sqrt(discriminant)) / (2 * k1)));
    }

    double computeLightning(Vector P, Vector N) {
        var i = 0.0;
        Vector L;
        for (Light light : lights) {
            if (light.type == 0) {
                i += light.intensity;
            } else {
                if (light.type == 1) {
                    L = vctrSubs(light.dirpos, P);
                } else {
                    L = light.dirpos;
                }
                var nDotL = vctrDot(N, L);
                if (nDotL > 0)
                    i += (light.intensity * nDotL) / (vctrLen(N) * vctrLen(L));
            }
        }
        return i;
    }

    int[][] getTracedArray() {
        int[][] array = new int[sizeX][sizeY];
        for (int i = -sizeY / 2; i < sizeY / 2; i++) {
            for (int j = -sizeX / 2; j < sizeX / 2; j++) {
                var D = canvasToViewPort(j, i);
                var clr = traceRay(pointOfView, D, 1, Integer.MAX_VALUE);
                array[sizeX / 2 + j][sizeY / 2 - i - 1] = clr;
            }
        }
        return array;
    }


}
