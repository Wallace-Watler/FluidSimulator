package main;

public class Vector {

    public static final Vector ZERO = new Vector(0, 0);

    public final double x;
    public final double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector v) {
        return new Vector(x + v.x, y + v.y);
    }

    public Vector subtract(Vector v) {
        return new Vector(x - v.x, y - v.y);
    }

    public Vector scale(double factor) {
        return new Vector(x * factor, y * factor);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }
}
