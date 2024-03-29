package raytracer.objects;

import raytracer.utils.Intersection;
import raytracer.utils.Vector3;

public class Plane extends SceneObject {
    private Vector3 origin;
    private double width;
    private double length;

    public Plane(Vector3 origin, double width, double length, Material material) {
        super(material, true);
        this.origin = origin;
        this.width = width;
        this.length = length;
    }

    @Override
    public Intersection intersect(Vector3 rayOrigin, Vector3 rayDirection) {
        double t = (origin.getY() - rayOrigin.getY()) / rayDirection.getY();
        if (t <= 0) {
            return null;
        }
        Vector3 point = rayOrigin.add(rayDirection.scale(t));

        double xCoord = point.getX() - origin.getX();
        double zCoord = point.getZ() - origin.getZ();
        if (xCoord < 0 || xCoord > width || zCoord < 0 || zCoord > length) {
            return null;
        }

        return new Intersection(t, point, Vector3.UP, this);
    }
}