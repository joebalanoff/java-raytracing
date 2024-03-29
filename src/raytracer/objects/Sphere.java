package raytracer.objects;

import raytracer.utils.Intersection;
import raytracer.utils.Vector3;

public class Sphere extends SceneObject {
    public Vector3 center;
    private double radius;

    public Sphere(Vector3 center, double radius, Material material) {
        super(material, true);
        this.center = center;
        this.radius = radius;
    }
    
    @Override
    public Intersection intersect(Vector3 origin, Vector3 direction) {
        Vector3 oc = origin.subtract(this.center);
        double a = direction.dotProduct(direction);
        double b = 2.0 * oc.dotProduct(direction);
        double c = oc.dotProduct(oc) - this.radius * this.radius;
        double discriminant = b * b - 4 * a * c;

        if (discriminant > 0) {
            double t = (-b - Math.sqrt(discriminant)) / (2 * a);
            if (t > 0) {
                Vector3 point = origin.add(direction.scale(t));
                Vector3 normal = point.subtract(this.center).normalize();

                return new Intersection(t, point, normal, this);
            }
        }

        return null;
    }
}
