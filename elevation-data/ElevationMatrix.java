
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.io.PrintWriter;

public class ElevationMatrix {
    public static double[][] elevationMatrix=new double[100][100];

    public static void main(String[] args) throws java.io.FileNotFoundException {
        String imagePath = "C:\\Users\\danie\\Desktop\\comsim\\elevation.png"; // Input PNG path
        int targetSize = 100;

        try {
            BufferedImage original = ImageIO.read(new File(imagePath));

            // Resample to 100x100
            Image scaledImage = original.getScaledInstance(targetSize, targetSize, Image.SCALE_SMOOTH);
            BufferedImage resized = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = resized.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            //double[][] elevationMatrix = new double[targetSize][targetSize];

            for (int y = 0; y < targetSize; y++) {
                for (int x = 0; x < targetSize; x++) {
                    int rgb = resized.getRGB(x, y);
                   
                    Color color = new Color(rgb);
                    int gray =4* color.getRed(); // or getGreen(), getBlue()
                     System.out.println("Test"+gray);
                    double elevationMeters = 26 + gray * (2000.0 - 26) / 255.0;
                    elevationMatrix[y][x] = gray;
                }
            }

            // Test print
            try (PrintWriter pw = new PrintWriter("elevation.csv")) {
                for (int y = 0; y < 100; y++) {
                    for (int x = 0; x < 100; x++) {
                        pw.print(elevationMatrix[y][x]);
                        if (x < 99) pw.print(",");
                    }
                    pw.println();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}