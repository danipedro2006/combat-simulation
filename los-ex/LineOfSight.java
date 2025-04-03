
public class LineOfSight {
    
    // Method to check if point (x3, y3, z3) obstructs the line of sight from (x1, y1, z1) to (x2, y2, z2)
    public static boolean isPointObstructing(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3) {
        // Step 1: Check if point (x3, y3) is on the line segment between (x1, y1) and (x2, y2)
        if (!isPointOnLine(x1, y1, x2, y2, x3, y3)) {
            return false; // If point is not on the line, it cannot obstruct the line of sight
        }
        
        // Step 2: Check if the altitude of point (x3, y3) is higher than the expected altitude on the line
        double expectedZ = getExpectedAltitude(x1, y1, z1, x2, y2, z2, x3, y3);
        
        // If z3 > expectedZ, point (x3, y3, z3) obstructs the line of sight
        return z3 > expectedZ;
    }

    // Method to check if point (x3, y3) is on the line segment between (x1, y1) and (x2, y2)
    private static boolean isPointOnLine(double x1, double y1, double x2, double y2, double x3, double y3) {
        // Cross product for collinearity check
        double crossProduct = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
        if (crossProduct != 0) {
            return false; // Points are not collinear
        }
        
        // Check if point (x3, y3) lies between (x1, y1) and (x2, y2)
        return (x3 >= Math.min(x1, x2) && x3 <= Math.max(x1, x2)) && (y3 >= Math.min(y1, y2) && y3 <= Math.max(y1, y2));
    }

    // Method to calculate the expected altitude at (x3, y3) based on linear interpolation between (x1, y1, z1) and (x2, y2, z2)
    private static double getExpectedAltitude(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3) {
        // Calculate distances
        double d12 = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double d13 = Math.sqrt(Math.pow(x3 - x1, 2) + Math.pow(y3 - y1, 2));
        
        // Linear interpolation for z
        return z1 + ((z2 - z1) / d12) * d13;
    }

    public static void main(String[] args) {
        // Test case: check if (x3, y3, z3) obstructs the line from (x1, y1, z1) to (x2, y2, z2)
        double x1 = 0, y1 = 0, z1 = 0; // Start point
        double x2 = 10, y2 = 10, z2 = 10; // End point
        double x3 = 5, y3 = 5, z3 = 7; // Point to check for obstruction

        boolean obstructing = isPointObstructing(x1, y1, z1, x2, y2, z2, x3, y3, z3);
        System.out.println("Does the point obstruct the line of sight? " + obstructing);
    }
}
