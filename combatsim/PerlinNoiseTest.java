 
import java.util.*;

public class PerlinNoiseTest {
    public static void main(String[] args) {
        System.out.println("Testing Perlin Noise Generation...\n");
         Random rand = new Random();

        double z= rand.nextDouble();

        // Generate and print noise values for a range of inputs
        for (int x = 0; x < 200; x ++) {
            for (int y = 0; y < 200; y ++) {
               
                    int noiseValue =(int) PerlinNoiseUtil.noise(x, y, z);
                    System.out.printf("Noise at (%d, %d):%d%n", x, y, noiseValue);
                
            }
        }
    }
}
