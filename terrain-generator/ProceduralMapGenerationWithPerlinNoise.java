import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public final class ProceduralMapGenerationWithPerlinNoise
{

    private static final int DEFAULT_HEIGHT = 200;

    private static final int DEFAULT_WIDTH = 200;

    public static void main(String[] args)
    {
        int height = DEFAULT_HEIGHT;
        int width = DEFAULT_WIDTH;
        long seed = System.currentTimeMillis();
        if (args.length == 3)
        {
            height = Integer.parseInt(args[0]);
            width = Integer.parseInt(args[1]);
            seed = Long.parseLong(args[2]);
        }

        Random rand = new Random(seed);
        double z = rand.nextDouble();

        JFrame window = new JFrame();
        window.setSize(width * 10, height * 10);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().add(new Map(height, width, z));
        window.setVisible(true);
        window.setTitle("Seed: " + seed);
    }

    public static class Map extends JPanel
    {
        private int height;

        private int width;

        private double z;

        public Map(int height, int width, double z)
        {
            this.height = height;
            this.width = width;
            this.z = z;
        }

        @Override
        public void paint(Graphics g)
        {
            for (int i = 0; i < width; ++i)
            { // y
                for (int j = 0; j < height; ++j)
                { // x
                    double x = (double)j / ((double)width);
                    double y = (double)i / ((double)height);
                    double n = noise(10 * x, 10 * y, z);

                    // Watter (or a Lakes)
                    if (n < 0.35)
                    {
                        g.setColor(Color.CYAN);
                    }
                    // Floors (or Planes)
                    else if (n >= 0.35 && n < 0.7)
                    {
                        g.setColor(Color.GREEN);
                    }
                    // Walls (or Mountains)
                    else if (n >= 0.7 && n < 0.85)
                    {
                        g.setColor(Color.GRAY);
                    }
                    // Ice (or Snow)
                    else
                    {
                        g.setColor(Color.white);
                    }
                    g.fillRect(i * 10, j * 10, 10, 10);
                }
            }
        }

        private double noise(double x, double y, double z)
        {
            // Find the unit cube that contains the point
            int X = (int)Math.floor(x) & 255;
            int Y = (int)Math.floor(y) & 255;
            int Z = (int)Math.floor(z) & 255;

            // Find relative x, y,z of point in cube
            x -= Math.floor(x);
            y -= Math.floor(y);
            z -= Math.floor(z);

            // Compute fade curves for each of x, y, z
            double u = fade(x);
            double v = fade(y);
            double w = fade(z);

            // Hash coordinates of the 8 cube corners
            int A = p[X] + Y;
            int AA = p[A] + Z;
            int AB = p[A + 1] + Z;
            int B = p[X + 1] + Y;
            int BA = p[B] + Z;
            int BB = p[B + 1] + Z;

            // Add blended results from 8 corners of cube
            double res = lerp(
                    w,
                    lerp(v, lerp(u, grad(p[AA], x, y, z), grad(p[BA], x - 1, y, z)),
                            lerp(u, grad(p[AB], x, y - 1, z), grad(p[BB], x - 1, y - 1, z))),
                    lerp(v, lerp(u, grad(p[AA + 1], x, y, z - 1), grad(p[BA + 1], x - 1, y, z - 1)),
                            lerp(u, grad(p[AB + 1], x, y - 1, z - 1), grad(p[BB + 1], x - 1, y - 1, z - 1))));
            return (res + 1.0) / 2.0;
        }

        private double fade(double t)
        {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        private double lerp(double t, double a, double b)
        {
            return a + t * (b - a);
        }

        private double grad(int hash, double x, double y, double z)
        {
            int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
            double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
            v = h < 4 ? y : h == 12 || h == 14 ? x : z;
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }

        private static final int p[] = new int[512];

        private static final int permutation[] = { 151, 160, 137, 91, 90, 15,
            131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
            190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33,
            88, 237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166,
            77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40, 244,
            102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
            135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123,
            5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
            223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
            129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
            251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107,
            49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254,
            138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180
        };

        static
        {
            for (int i = 0; i < 256; i++)
                p[256 + i] = p[i] = permutation[i];
        }
    }
}