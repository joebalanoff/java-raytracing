package raytracer.utils;

import raytracer.objects.SceneObject;

public class Intersection {
    public double t;
    public Vector3 point;
    public Vector3 normal;
    public SceneObject object;

    public Intersection(double t, Vector3 point, Vector3 normal, SceneObject object) {
        this.t = t;
        this.point = point;
        this.normal = normal;
        this.object = object;
    }
}
