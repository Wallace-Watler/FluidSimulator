package main.math;

public class Vec2d {

    public static final Vec2d ZERO = new Vec2d(0, 0);

    public final double x, y;

    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2d add(Vec2d v) {
        return new Vec2d(x + v.x, y + v.y);
    }

    public Vec2d subtract(Vec2d v) {
        return new Vec2d(x - v.x, y - v.y);
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vec2d scale(double factor) {
        return new Vec2d(x * factor, y * factor);
    }

    public Vec2d normalize() {
        return scale(1 / length());
    }
}
