 
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.awt.Image;
import java.awt.Graphics;
import javax.swing.ImageIcon;


class Terrain {
    public static int scale=10;
    public static double metricdistance;
    public static int griddistance;

    public static int MAP_SIZE = Constants.MAP_SIZE;
    public static int CELL_SIZE =Constants.CELL_SIZE;
    public static int GRID_SIZE = MAP_SIZE/CELL_SIZE;

    public static final int MAX_ALTITUDE = Constants. MAX_ALTITUDE;

    
    public static double[][] altitudeGrid = new double[GRID_SIZE][GRID_SIZE];
    public static boolean[][] obstructingCells = new boolean[GRID_SIZE][GRID_SIZE];

    private static Image mapImage;

    // Initialize terrain data
    static {
        try {
            mapImage = new ImageIcon("C:\\Users\\danie\\Desktop\\comsim\\map.png").getImage();
        } catch (Exception e) {
            System.err.println("Failed to load terrain image: " + e.getMessage());
        }
        loadTerrainElevation();
    }

    public static void drawMap(Graphics g, int width, int height) {
        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, width, height, null);
        }
    }
    
    
    public static int getMapPixelWidth() {
    if (mapImage != null) {
        return mapImage.getWidth(null);
    }
    return -1; // Or throw exception if image is not loaded
}


 public static int getMapPixelHeight() {
    if (mapImage != null) {
        return mapImage.getWidth(null);
    }
    return -1; // Or throw exception if image is not loaded
}

    
    public static void loadTerrainElevation(){
        String csvPath = "C:\\Users\\danie\\Desktop\\comsim\\elevation-data\\elevation.csv"; // path to your CSV file
        List<double[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                double[] row = new double[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Double.parseDouble(tokens[i].trim());
                }
                rows.add(row);
            }

            // Convert List<int[]> to int[][]
            altitudeGrid = rows.toArray(new double[0][]);

            // Example: print a value
            //System.out.println("Elevation at (0,0): " + altitudeGrid[0][0]);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Get altitude at a specific grid cell
    public static double getAltitude(int x, int y) {
        return altitudeGrid[x][y];

    }
    
    public static int distanceToGridMetric(double metricdistance){
        griddistance=(int)metricdistance/scale;
        
        return griddistance;

    }
    
    public static  double gridTodistance(int griddistance){
        metricdistance=griddistance*scale;
        return metricdistance;

    }

    public static boolean checkLineOfSight(int x1, int y1, double z1, int x2, int y2, double z2) {
        System.out.println("Checking Line of Sight...");
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

        return !hasObstruction; // Returns true if line of sight is clear
    }

    private static boolean isPointObstructing(int x1, int y1, double z1, int x2, int y2, double z2, int x3, int y3, double z3) {
        if (!isPointOnLine(x1, y1, x2, y2, x3, y3)) {
            return false; // If point is not on the line, it cannot obstruct
        }

        double expectedZ = getExpectedAltitude(x1, y1, z1, x2, y2, z2, x3, y3);
        return z3 > expectedZ; // If actual altitude is greater, it blocks LOS
    }

    // Check if (x3, y3) is on the line segment from (x1, y1) to (x2, y2)
    private static boolean isPointOnLine(int x1, int y1, int x2, int y2, int x3, int y3) {
        int crossProduct = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
        if (crossProduct != 0) return false;

        return (x3 >= Math.min(x1, x2) && x3 <= Math.max(x1, x2)) && (y3 >= Math.min(y1, y2) && y3 <= Math.max(y1, y2));
    }

    // Calculate expected altitude at (x3, y3) based on linear interpolation
    private static double getExpectedAltitude(int x1, int y1, double z1, int x2, int y2, double z2, int x3, int y3) {
        double d12 = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double d13 = Math.sqrt(Math.pow(x3 - x1, 2) + Math.pow(y3 - y1, 2));

        return (int) (z1 + ((z2 - z1) / d12) * d13);
    }

    // Check if a cell is obstructed
    public static boolean isObstructed(int x, int y) {
        return obstructingCells[x][y];
    }

}
