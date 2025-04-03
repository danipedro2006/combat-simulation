import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PerlinNoise {
    private int[] permutation;

    public PerlinNoise() {
        permutation = new int[512];
        // Standard Perlin permutation table with exactly 256 values
        int[] p = {151, 160, 137, 91, 90, 15, 131, 13, 199, 196, 1, 39, 114, 136, 158, 97, 
                    15, 77, 63, 37, 9, 55, 92, 48, 98, 98, 3, 119, 19, 24, 14, 42, 63, 64, 9, 
                    20, 42, 99, 82, 12, 33, 109, 54, 48, 101, 58, 7, 75, 115, 45, 111, 10, 9, 
                    48, 19, 9, 53, 40, 51, 65, 41, 79, 2, 30, 11, 69, 63, 34, 50, 72, 22, 60, 
                    56, 34, 29, 72, 43, 85, 25, 36, 40, 88, 49, 48, 19, 75, 19, 31, 79, 97, 
                    8, 51, 3, 9, 100, 50, 63, 76, 82, 100, 41, 50, 75, 34, 98, 96, 8, 39, 17, 
                    95, 6, 19, 1, 90, 25, 97, 100, 17, 2, 21, 32, 7, 93, 63, 22, 92, 97, 38, 
                    90, 48, 9, 58, 25, 63, 51, 17, 98, 38};

        // Fill the permutation array
        for (int i = 0; i < 256; i++) {
            permutation[i] = p[i]; 
            permutation[i + 256] = p[i]; // Duplicate for overflow handling
        }
    }

    public double noise(double x, double y) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);

        double u = fade(x);
        double v = fade(y);

        int A = (permutation[X] + Y) & 255;
        int B = (permutation[X + 1] + Y) & 255;

        return lerp(v, lerp(u, grad(permutation[A], x, y),
                               grad(permutation[B], x - 1, y)),
                       lerp(u, grad(permutation[A + 1], x, y - 1),
                               grad(permutation[B + 1], x - 1, y - 1)));
    }

    private static double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }
    private static double lerp(double t, double a, double b) { return a + t * (b - a); }
    private static double grad(int hash, double x, double y) {
        int h = hash & 3;
        double u = h < 2 ? x : y;
        double v = h < 2 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? 2 * v : -2 * v);
    }

    // Main method for visualization
    public static void main(String[] args) {
        // Create a frame
        JFrame frame = new JFrame("Perlin Noise Terrain");
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create an image to display terrain
        int width = 400;
        int height = 400;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Create an instance of PerlinNoise
        PerlinNoise perlinNoise = new PerlinNoise();

        // Generate terrain using Perlin noise and set the image pixels
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double nx = x / (double) width;
                double ny = y / (double) height;
                double noiseValue = perlinNoise.noise(nx * 10, ny * 10); // Adjust scale here
                int colorValue = (int) ((noiseValue + 1) * 127.5);  // Normalize to range 0-255
                Color color = new Color(colorValue, colorValue, colorValue);  // Grayscale color

                image.setRGB(x, y, color.getRGB());
            }
        }

        // Display the image in a JLabel
        JLabel label = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(label);

        // Show the frame
        frame.setVisible(true);
    }
}
