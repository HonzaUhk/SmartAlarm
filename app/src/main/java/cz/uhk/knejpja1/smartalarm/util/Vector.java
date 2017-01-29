package cz.uhk.knejpja1.smartalarm.util;

public class Vector {
    private float x = 0.0f;
    private float y = 0.0f;
    private float z = 0.0f;

    public Vector() {
    }

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector add(Vector other) {
        return new Vector(x + other.x(), y + other.y(), z + other.z());
    }

    public Vector mult(float val) {
        return new Vector(x * val, y * val, z * val);
    }

    public double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float z() {
        return this.z;
    }

    public String toString() {
        return "[" + x + ", " + y + ", " + z + "]";
    }
}
