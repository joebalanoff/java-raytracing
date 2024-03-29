package raytracer.objects;

import java.awt.Color;

public class Material {
    private Color albedo;
    private double metallic;
    private double roughness;

    public Material(Color albedo, double metallic, double roughness) {
        this.albedo = albedo;
        this.metallic = metallic;
        this.roughness = roughness;
    }

    public Color getAlbedo() { return albedo; }
    public double getMetallic() { return metallic; }
    public double getRoughness() { return roughness; }
}
