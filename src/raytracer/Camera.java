package raytracer;

import java.awt.event.KeyEvent;

import raytracer.utils.Vector3;

public class Camera {
    private Vector3 position;
    private double yaw;
    private double pitch;
    public double fov;
    public double aspectRatio;

    private double cameraSpeed = 0.1;

    public Camera(Vector3 position, double yaw, double pitch, double fov, double aspectRatio) {
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
    }

    public void update(int dx, int dy, boolean[] keys) {
        yaw += dx * 0.01;
        pitch += dy * 0.01;
        pitch = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, pitch));
    
        Vector3 direction = getDirection();
        System.out.println(direction);
    
        Vector3 right = direction.crossProduct(Vector3.UP).normalize();
    
        if (keys[KeyEvent.VK_A]) {
            position = position.add(direction.scale(cameraSpeed));
        }
        if (keys[KeyEvent.VK_D]) {
            position = position.subtract(direction.scale(cameraSpeed));
        }
        if (keys[KeyEvent.VK_W]) {
            position = position.subtract(right.scale(cameraSpeed));
        }
        if (keys[KeyEvent.VK_S]) {
            position = position.add(right.scale(cameraSpeed));
        }
        if (keys[KeyEvent.VK_SPACE]) {
            position = position.add(Vector3.UP.scale(cameraSpeed));
        }
        if (keys[KeyEvent.VK_SHIFT]) {
            position = position.subtract(Vector3.UP.scale(cameraSpeed));
        }
    }
       

    public Vector3 getDirection() {
        double x = Math.cos(pitch) * Math.sin(yaw);
        double y = Math.sin(pitch);
        double z = Math.cos(pitch) * Math.cos(yaw);
        return new Vector3(x, y, z).normalize();
    }

    public Vector3 getPosition() { return position; }

    public double getYaw() { return yaw; }
    public double getPitch() { return pitch; }

    public void mouseDragged(int dx, int dy) {
        System.out.println(dx + "," + dy);
        yaw += dx * 0.01;
        pitch += dy * 0.01;
        pitch = Math.max(-Math.PI / 2, Math.min(Math.PI / 2, pitch));
    }
}
