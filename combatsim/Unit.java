 
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;

public class Unit {
    public static int UNIT_L=Constants.UNIT_L;
    public static int UNIT_W=Constants.UNIT_W;
    public int x, y;
    public int z;         // Position on the grid
    public double killProbability;  // Probability of a successful attack
    public double range;     // Attack range in grid units
    public String team;      // "Red" or "Blue"
    public String type;
    public static int code=0;
    public double sensorRange=Constants.sensorRange; // e.g., 100.0
    public boolean visible = Constants.visible;
    

    private List<Point> path = new ArrayList<>();

    public static class Point {
        public final int x;
        public final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }

    public Unit(int x, int y,int z, double killProbability, double range, String team, String type) {
        this.x = x;
        this.y = y;
        this.z =(int) Terrain.getAltitude(x, y);
        this.killProbability = killProbability;
        this.range = range;
        this.team = team;
        this.type=type;
        this.code++;
    }
    
    public boolean isInSensorRangeOf(Unit observer) {
        boolean isVisible=false;
        double dx = this.x - observer.x;
        double dy = this.y - observer.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if((distance<=observer.sensorRange) && (Terrain.checkLineOfSight(this.x, this.y, this.z, observer.x, observer.y, observer.z))){
            return true;
        }
        return false;
    }

    public List<Point> getPath() {
        return path;
    }

    public void drawUnit(int x, int y, Graphics g2, Color color){
        g2.setColor(color); // Fill color for friendly unit
        g2.fillRect(x, y, UNIT_L, UNIT_W);  // Fill the rectangle (unit frame)

        g2.setColor(Color.BLACK); // Outline color for friendly unit
        g2.drawRect(x, y, UNIT_L, UNIT_W);  // Draw the outline of the rectangle

        g2.setColor(Color.BLACK); // Cross for infantry symbol
        g2.drawLine(x, y, x+UNIT_L, y+UNIT_W);  // Diagonal "X" line 1
        g2.drawLine(x+UNIT_L, y, x, y+UNIT_W);  // Diagonal "X" line 2

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(""+code, x, y); 
    }

    public void move() {
        if (path.size() > 1) {  // We need at least two points: current + next
            Point next = path.get(1);     // Peek at next point
            this.x = next.x;
            this.y = next.y;
            path.remove(0);              // Remove the current point (just passed)
        } else if (path.size() == 1) {
            path.remove(0);              // Clear the last point
        }

    }

    public void addPathPoint(Point p) {
        if (path.isEmpty()) {
            Point firstPoint = new Point(this.x, this.y);
            path.add(firstPoint);
        }
        path.add(p);
        System.out.println("Path now: " + path);

    }

    // Check if this unit can attack another unit based on range
    public boolean canAttack(Unit target) {
        double distance = Math.sqrt(Math.pow(this.x - target.x, 2) + Math.pow(this.y - target.y, 2));
        if ((distance <= this.range) && (Terrain.checkLineOfSight(this.x, this.y, this.z, target.x, target.y, target.z))) {
            return true;
        }
        else{
            System.out.println("Can't attack:"+target.x+"-"+ target.y);
            return false; // Ensure there's always a return value
        }

    }

    @Override
    public String toString() {
        return "Team: "+team + "\n"+"Type: "+type+"\n"+"Grid (" + x + ", " + y + ")"+"\n"+"Kill Prob: " + killProbability +"\n"+ "Range: " + range+"\n"+"Sensor range: "+sensorRange+"\n"+"Visibility: "+visible;
    }
}
