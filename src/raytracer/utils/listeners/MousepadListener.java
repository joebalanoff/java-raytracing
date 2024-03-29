package raytracer.utils.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import raytracer.Camera;

public class MousepadListener implements MouseMotionListener, MouseListener {
    private final Camera camera;
    private int lastMouseX;
    private int lastMouseY;

    public MousepadListener(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void mouseDragged(MouseEvent e){
        int dx = e.getX() - lastMouseX;
        int dy = e.getY() - lastMouseY;
        camera.mouseDragged(dx, dy);
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) { }
}
