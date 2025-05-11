package raytracer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import raytracer.objects.Plane;
import raytracer.objects.Material;
import raytracer.objects.SceneObject;
import raytracer.objects.Sphere;
import raytracer.utils.Intersection;
import raytracer.utils.Time;
import raytracer.utils.Vector3;
import raytracer.utils.listeners.KeyboardListener;
import raytracer.utils.listeners.MousepadListener;

public class RaytracerGUI extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private BufferedImage image;

    private List<SceneObject> sceneObjects;
    private List<Light> lights;

    private Camera camera;

    public RaytracerGUI(Camera camera) {
        this.camera = camera;

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        sceneObjects = new ArrayList<>();
        
        sceneObjects.add(new Plane(
            new Vector3(-5, -1, 1),  
            10,                    
            10,                    
            new Material(Color.GRAY, .5, .5)
        ));

        sceneObjects.add(new Sphere(new Vector3(-2.5, 0, 5), 1, new Material(Color.red, .5, .5)));
        sceneObjects.add(new Sphere(new Vector3(0, 0, 5), 1, new Material(Color.green, .5, .5)));
        sceneObjects.add(new Sphere(new Vector3(2.5, 0, 5), 1, new Material(Color.blue, .5, .5)));

        lights = new ArrayList<>();
        lights.add(new Light(new Vector3(0, 5, 0), Color.WHITE, .8));

        render();
        startRenderingLoop();
    }

    private void startRenderingLoop(){
        final int targetFPS = 30;
        final long targetFrameTime = 1000000000 / targetFPS;

        new Thread(() -> {
            long prevTime = System.nanoTime();
            while (true) {
                long currentTime = System.nanoTime();
                long elapsedTime = currentTime - prevTime;

                prevTime = currentTime;
                Time.deltaTime = (double) elapsedTime / 1000000000;

                update();
                render();
                repaint();

                long sleepTime = Math.max(0, targetFrameTime - elapsedTime);

                try {
                    Thread.sleep(sleepTime / 1000000, (int) (sleepTime % 1000000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void update(){
        camera.update(0, 0, KeyboardListener.keys);
    }

    private void render() {
        int numThreads = Runtime.getRuntime().availableProcessors() - 2;
        if(numThreads <= 0) numThreads = 1;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for(int y = 0; y < HEIGHT; y++) {
            final int row = y;
            executor.execute(() -> renderRow(row));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void renderRow(int row) {
        final int maxBounces = 3;

        for (int x = 0; x < WIDTH; x++) {
            double ndcX = (2 * (x + 0.5) / WIDTH) - 1;
            double ndcY = (1 - 2 * (row + 0.5) / HEIGHT);

            double tanHalfFov = Math.tan(camera.fov / 2);
            double cameraX = ndcX * camera.aspectRatio * tanHalfFov;
            double cameraY = ndcY * tanHalfFov;
            Vector3 rayDirection = new Vector3(cameraX, cameraY, -1).normalize();
            Vector3 cameraDirection = camera.getDirection();

            rayDirection = rayDirection.rotateY(cameraDirection.getYaw()).rotateX(cameraDirection.getPitch());

            Color pixelColor = traceRay(camera.getPosition(), rayDirection, maxBounces);

            image.setRGB(x, row, pixelColor.getRGB());
        }
    }
    
    private Color traceRay(Vector3 origin, Vector3 direction, int remainingBounces) {
        if (remainingBounces <= 0) return Color.BLACK;
        
        Intersection closestIntersection = findClosestIntersection(origin, direction);
        
        if (closestIntersection == null) return Color.BLACK;
    
        Color albedoColor = closestIntersection.object.material.getAlbedo();
        double metallic = closestIntersection.object.material.getMetallic();
        double roughness = closestIntersection.object.material.getRoughness();
        double reflectivity = (1 - metallic) + metallic * (1 - roughness);
    
        int r = albedoColor.getRed();
        int g = albedoColor.getGreen();
        int b = albedoColor.getBlue();
    
        if (closestIntersection.object.isReflective()) {
            Vector3 reflectedDirection = calculateReflection(direction, closestIntersection.normal);
            Color reflectedColor = traceRay(closestIntersection.point, reflectedDirection, remainingBounces - 1);
    
            r = (int) (r * (1 - reflectivity) + reflectedColor.getRed() * reflectivity);
            g = (int) (g * (1 - reflectivity) + reflectedColor.getGreen() * reflectivity);
            b = (int) (b * (1 - reflectivity) + reflectedColor.getBlue() * reflectivity);
        }
    
        for (Light light : lights) {
            double intensity = light.getIntensity();
            r *= intensity;
            g *= intensity;
            b *= intensity;

            Vector3 lightDirection = light.getPosition().subtract(closestIntersection.point).normalize();
    
            double diffuseIntensity = Math.max(0, closestIntersection.normal.dotProduct(lightDirection));
            Color diffuseColor = new Color(
                (int) (albedoColor.getRed() * diffuseIntensity),
                (int) (albedoColor.getGreen() * diffuseIntensity),
                (int) (albedoColor.getBlue() * diffuseIntensity)
            );
    
            Vector3 viewDirection = origin.subtract(closestIntersection.point).normalize();
            Vector3 halfVector = lightDirection.add(viewDirection).normalize();
            double specularIntensity = Math.pow(Math.max(0, closestIntersection.normal.dotProduct(halfVector)), 50.0); // SPECULAR EXPONENT (50.0)
            Color specularColor = new Color(
                (int) (light.getColor().getRed() * specularIntensity * intensity),
                (int) (light.getColor().getGreen() * specularIntensity * intensity),
                (int) (light.getColor().getBlue() * specularIntensity * intensity)
            );
    
            boolean inShadow = false;
            for (SceneObject object : sceneObjects) {
                if (object != closestIntersection.object) {
                    Intersection shadowIntersection = object.intersect(closestIntersection.point, lightDirection);
                    if (shadowIntersection != null && shadowIntersection.t < lightDirection.magnitude()) {
                        inShadow = true;
                        break;
                    }
                }
            }
    
            if (!inShadow) {
                r += (diffuseColor.getRed() + specularColor.getRed()) * intensity;
                g += (diffuseColor.getGreen() + specularColor.getGreen()) * intensity;
                b += (diffuseColor.getBlue() + specularColor.getBlue()) * intensity;
            }
        }
    
        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));
    
        return new Color(r, g, b);
    }    

    private Intersection findClosestIntersection(Vector3 origin, Vector3 direction) {
        Intersection closestIntersection = null;
        double closestDistance = Double.MAX_VALUE;

        for (SceneObject object : sceneObjects) {
            Intersection intersection = object.intersect(origin, direction);
            if (intersection != null) {
                double distance = origin.distanceTo(intersection.point);
                if (distance < closestDistance) {
                    closestIntersection = intersection;
                    closestDistance = distance;
                }
            }
        }

        return closestIntersection;
    }

    private Vector3 calculateReflection(Vector3 incident, Vector3 normal) {
        return incident.subtract(normal.scale(2 * incident.dotProduct(normal)));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(image, 0, 0, this);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Raytracer GUI");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            Camera camera = new Camera(new Vector3(0, 0, -1.5), -(Math.PI / 2), 0, Math.toRadians(60), (double) WIDTH / HEIGHT);

            RaytracerGUI raytracerGUI = new RaytracerGUI(camera);
            frame.add(raytracerGUI);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            frame.addKeyListener(new KeyboardListener());
            frame.addMouseMotionListener(new MousepadListener(camera));

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    saveImageToFile(raytracerGUI.image);
                }
            });
        });
    }

    private static void saveImageToFile(BufferedImage image) {
        File outputFile = new File("render.png");
        try {
            ImageIO.write(image, "png", outputFile);
            System.out.println("Image saved to " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }
}
