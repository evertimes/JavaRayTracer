package com.evertimes;

import com.evertimes.datatype.DoublePair;
import com.evertimes.datatype.DoubleSpherePair;
import com.evertimes.datatype.Light;
import com.evertimes.datatype.Matrix;
import com.evertimes.datatype.Sphere;
import com.evertimes.datatype.Vector;
import org.jetbrains.skija.Color;

import java.util.List;

import static java.lang.Math.sqrt;
import static com.evertimes.datatype.Vector.*;

public class RayTracer {
    final int sizeX;
    final int sizeY;

    public RayTracer(int width, int height) {
        this.sizeX = width;
        this.sizeY = height;
    }

    static final int BACKGROUND_COLOR = Color.makeRGB(0, 0, 0);
    private static final double step = 0.5;
    private static final double rotationStepRadians = 0.0872665;
    private double currentRotationRadians = 0.0;
    int Vh = 1; //Height of view window
    int Vw = 1; //Width of view window
    double dist = 1.0;
    List<Sphere> spheres = List.of(
            new Sphere(new Vector(0.0, -1.0, 3.0), 1.0, 0xFF0000, 500, 0.2),
            new Sphere(new Vector(2.0, 0.0, 4.0), 1.0, 0x0000FF, 500, 0.3),
            new Sphere(new Vector(-2.0, 0.0, 4.0), 1.0, 0x00FF00, 10, 0.4),
            new Sphere(new Vector(0.0, -5001.0, 0.0), 5000.0, 0xFFFFFF, 1000, 0.5));
    List<Light> lights = List.of(
            new Light(0, 0.2, new Vector(0.0, 0.0, 0.0)),
            new Light(1, 0.6, new Vector(2.0, 1.0, 0.0)),
            new Light(2, 0.2, new Vector(1.0, 4.0, 4.0)));
    Vector pointOfView = new Vector(0.0, 0.0, -2.0);
    double[][] cameraRotation = {{0.7071, 0, -0.7071}, {0, 1, 0}, {0.7071, 0, 0.7071}};
    double EPSILON = 0.001;
    int recursionDepth = 2;

    public void incZView() {
        pointOfView.z += step;
    }

    public void decZView() {
        pointOfView.z -= step;
    }

    public void incXView() {
        pointOfView.x += step;
    }

    public void decXView() {
        pointOfView.x -= step;
    }

    public void decYView() {
        pointOfView.y -= step;
    }

    public void incYView() {
        pointOfView.y += step;
    }

    public void rotateRight() {
        currentRotationRadians += rotationStepRadians;
    }

    public void rotateLeft() {
        currentRotationRadians -= rotationStepRadians;
    }

    Vector canvasToViewPort(int x, int y) {
        return new Vector(((double) x) / sizeX, ((double) y) / sizeY, dist);
    }

    Vector reflectRay(Vector v1, Vector v2) {
        return vctrSubs(vctrScale(v2, 2 * vctrDot(v1, v2)), v1);
    }

    DoubleSpherePair closestIntersection(Vector O, Vector D, double t_min, double t_max) {
        var closest_T = Double.POSITIVE_INFINITY;
        Sphere closest_sphere = null;
        for (Sphere sphere : spheres) {
            var t = intersectRaySphere(O, D, sphere);
            if (t.getT1() < closest_T && t_min < t.getT1() && t.getT1() < t_max) {
                closest_T = t.getT1();
                closest_sphere = sphere;
            }
            if (t.getT2() < closest_T && t_min < t.getT2() && t.getT2() < t_max) {
                closest_T = t.getT2();
                closest_sphere = sphere;
            }
        }
        if (closest_sphere != null) {
            return new DoubleSpherePair(closest_sphere, closest_T);
        } else {
            return null;
        }
    }

    int traceRay(Vector O, Vector D, double t_min, double t_max, int depth) {
        Vector view = vctrScale(D, -1);
        DoubleSpherePair intersection = closestIntersection(O, D, t_min, t_max);
        if (intersection == null) {
            return BACKGROUND_COLOR;
        }
        Sphere closest_sphere = intersection.sphere;
        double closest_T = intersection.doubleValue;
        var P = vctrSum(O, vctrScale(D, closest_T));
        var N = vctrSubs(P, closest_sphere.center);
        N = vctrScale(N, 1 / vctrLen(N));
        var colorFactor = computeLightning(P, N, view, closest_sphere.specular);
        var red = (Color.getR(closest_sphere.color) * colorFactor);
        if (red > 255)
            red = 255;
        var green = (Color.getG(closest_sphere.color) * colorFactor);
        if (green > 255)
            green = 255;
        var blue = (Color.getB(closest_sphere.color) * colorFactor);
        if (blue > 255)
            blue = 255;
        if (closest_sphere.reflective <= 0 || depth <= 0) {
            return Color.makeRGB((int) red, (int) green, (int) blue);
        }
        Vector reflectedRay = reflectRay(view, N);
        int reflectedColor = traceRay(P, reflectedRay, EPSILON, Double.POSITIVE_INFINITY, depth - 1);
        red = red * (1 - closest_sphere.reflective) + closest_sphere.reflective * Color.getR(reflectedColor);
        green = green * (1 - closest_sphere.reflective) + closest_sphere.reflective * Color.getG(reflectedColor);
        ;
        blue = blue * (1 - closest_sphere.reflective) + closest_sphere.reflective * Color.getB(reflectedColor);
        if (red > 255)
            red = 255;
        if (green > 255)
            green = 255;
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
            return new DoublePair(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        return new DoublePair(((k2 * (-1) + sqrt(discriminant)) / (2 * k1)), ((k2 * (-1) - sqrt(discriminant)) / (2 * k1)));
    }

    double computeLightning(Vector P, Vector N, Vector view, int specular) {
        var initialIntensity = 0.0;
        Vector L;
        double t_max;
        for (Light light : lights) {
            if (light.type == 0) {
                initialIntensity += light.intensity;
            } else {
                if (light.type == 1) {
                    L = vctrSubs(light.dirpos, P);
                    t_max = 1.0;
                } else {
                    L = light.dirpos;
                    t_max = Double.POSITIVE_INFINITY;
                }
                DoubleSpherePair blocker = closestIntersection(P, L, EPSILON, t_max);
                if (blocker != null) {
                    continue;
                }
                var nDotL = vctrDot(N, L);
                if (nDotL > 0) {
                    initialIntensity += (light.intensity * nDotL) / (vctrLen(N) * vctrLen(L));
                }
                //Specular Reflection
                if (specular != -1) {
                    Vector r = reflectRay(L, N);
                    double rv = vctrDot(r, view);
                    if (rv > 0) {
                        initialIntensity += light.intensity * Math.pow(rv / (vctrLen(r) * vctrLen(view)), specular);
                    }
                }
            }
        }
        return initialIntensity;
    }

    int[][] getTracedArray() {
        int[][] array = new int[sizeX][sizeY];
        for (int i = -sizeY / 2; i < sizeY / 2; i++) {
            for (int j = -sizeX / 2; j < sizeX / 2; j++) {
                var D = Matrix.multiplyMatrixByVector(Matrix.constructRotationYMatrix(currentRotationRadians), canvasToViewPort(j, i));
                var color = traceRay(pointOfView, D, 1, Integer.MAX_VALUE, recursionDepth);
                array[sizeX / 2 + j][sizeY / 2 - i - 1] = color;
            }
        }
        return array;
    }


}
