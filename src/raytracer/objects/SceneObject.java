package raytracer.objects;

import raytracer.utils.Intersection;
import raytracer.utils.Vector3;

public abstract class SceneObject {
    private boolean reflective;
    public Material material;

    public SceneObject(Material material, boolean reflective) {
        this.material = material;
        this.reflective = reflective;
    }

    public abstract Intersection intersect(Vector3 origin, Vector3 direction);

    public boolean isReflective() { return reflective; }
}
