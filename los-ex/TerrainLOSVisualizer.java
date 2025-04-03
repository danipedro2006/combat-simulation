import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class TerrainLOSVisualizer extends JPanel {
    private static final int GRID_SIZE = 200;
    private static final int MAX_ALTITUDE = 20;
    private static final int CELL_SIZE = 4; // Each grid cell is 20x20 pixels

    private static double[][] altitudeGrid = new double[GRID_SIZE][GRID_SIZE];
    private static boolean[][] obstructingCells = new boolean[GRID_SIZE][GRID_SIZE];

    public static void main(String[] args) {
        generateRandomGrid();

        // Select two random points
        Random rand = new Random();
        //int x1 = rand.nextInt(GRID_SIZE);
        int x1 = 0;
        int y1 =0;
        int x2 =199;
        int y2 =199;

        double z1 = altitudeGrid[x1][y1];
        double z2 = altitudeGrid[x2][y2];

        System.out.println("\nChecking LOS from (" + x1 + ", " + y1 + ") to (" + x2 + ", " + y2 + ")\n");

        checkLineOfSight(x1, y1, z1, x2, y2, z2);

        // Create window and panel
        JFrame frame = new JFrame("Terrain LOS Visualization");
        TerrainLOSVisualizer panel = new TerrainLOSVisualizer();
        frame.add(panel);
        frame.setSize(GRID_SIZE * CELL_SIZE + 20, GRID_SIZE * CELL_SIZE + 40);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Generate a random altitude grid
    private static void generateRandomGrid() {
        Random rand = new Random();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                altitudeGrid[i][j] = rand.nextInt(MAX_ALTITUDE);
                obstructingCells[i][j] = false; // Default: No obstruction
            }
        }
    }

    // Check LOS between two points and mark obstructing points
    private static void checkLineOfSight(int x1, int y1, double z1, int x2, int y2, double z2) {
        System.out.println("Obstructing Points:");
        boolean hasObstruction = false;

        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                if ((x == x1 && y == y1) || (x == x2 && y == y2)) continue; // Skip endpoints

                double z3 = altitudeGrid[x][y];
                if (isPointObstructing(x1, y1, z1, x2, y2, z2, x, y, z3)) {
                    System.out.println("Obstruction at (" + x + ", " + y + ") with altitude " + z3);
                    obstructingCells[x][y] = true; // Mark cell as obstructing
                    hasObstruction = true;
                }
            }
        }

        if (!hasObstruction) {
            System.out.println("No obstructions found.");
        }
    }

    // Check if a point obstructs the LOS between two points
    private static boolean isPointObstructing(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
        if (!isPointOnLine(x1, y1, x2, y2, x3, y3)) {
            return false; // If point is not on the line, it cannot obstruct
        }

        double expectedZ = getExpectedAltitude(x1, y1, z1, x2, y2, z2, x3, y3);
        return z3 > expectedZ; // If actual altitude is greater, it blocks LOS
    }

    // Check if (x3, y3) is on the line segment from (x1, y1) to (x2, y2)
    private static boolean isPointOnLine(double x1, double y1, double x2, double y2, double x3, double y3) {
        double crossProduct = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
        if (crossProduct != 0) return false;

        return (x3 >= Math.min(x1, x2) && x3 <= Math.max(x1, x2)) && (y3 >= Math.min(y1, y2) && y3 <= Math.max(y1, y2));
    }

    // Calculate expected altitude at (x3, y3) based on linear interpolation
    private static double getExpectedAltitude(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3) {
        double d12 = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double d13 = Math.sqrt(Math.pow(x3 - x1, 2) + Math.pow(y3 - y1, 2));

        return z1 + ((z2 - z1) / d12) * d13;
    }

    // Paint the grid
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                int altitude = (int) altitudeGrid[x][y];

                // Color: Green (low) → Red (high)
                if (obstructingCells[x][y]) {
                    g.setColor(Color.BLACK); // Obstructing points
                } else {
                    g.setColor(new Color(255 - altitude * 12, 255 - altitude * 5, 50));
                }

                g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE); // Grid outline
            }
        }
    }
}
