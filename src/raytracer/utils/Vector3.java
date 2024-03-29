package raytracer.utils;

public class Vector3 {
    public static final Vector3 UP = new Vector3(0, 1, 0);
    public static final Vector3 RIGHT = new Vector3(1, 0, 0);
    public static final Vector3 FORWARD = new Vector3(0, 0, 1);
    double x, y, z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector3 subtract(Vector3 other) {
        return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vector3 scale(double scalar) {
        return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
    }

    public double dotProduct(Vector3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector3 normalize() {
        double length = magnitude();
        return new Vector3(this.x / length, this.y / length, this.z / length);
    }

    public double distanceTo(Vector3 other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        double dz = other.z - this.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double magnitude() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vector3 crossProduct(Vector3 other) {
        double x = this.y * other.z - this.z * other.y;
        double y = this.z * other.x - this.x * other.z;
        double z = this.x * other.y - this.y * other.x;
        return new Vector3(x, y, z);
    }

    public Vector3 rotateX(double angle) {
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
        double newY = y * cosAngle - z * sinAngle;
        double newZ = y * sinAngle + z * cosAngle;
        return new Vector3(x, newY, newZ);
    }

    public Vector3 rotateY(double angle) {
        double cosAngle = Math.cos(angle);
        double sinAngle = Math.sin(angle);
        double newX = x * cosAngle + z * sinAngle;
        double newZ = -x * sinAngle + z * cosAngle;
        return new Vector3(newX, y, newZ);
    }

    public double getYaw() {
        return Math.atan2(z, x);
    }

    public double getPitch() {
        return Math.asin(y);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }
}
