package raytracer;

import java.awt.Color;

import raytracer.utils.Vector3;

public class Light {
    private Vector3 position;
    private Color color;
    private double intensity;

    public Light(Vector3 position, Color color, double intensity) {
        this.position = position;
        this.color = color;
        this.intensity = intensity;
    }

    public Vector3 getPosition() { return position; }
    public Color getColor() { return color; }
    public double getIntensity() { return intensity; }
}
