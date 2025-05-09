 
import java.util.*;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class ArtilleryUnit extends Unit {
    public int voleys=Constants.voleys;       // Number of shells per volley
    public static int fireRadius=Constants.fireRadius; // Scatter radius of shell impacts
    public int targetX, targetY; // Target coordinates
    public ArrayList<Unit.Point> impactPoints = new ArrayList<>();  
    public List<Unit> destroyed = new ArrayList<>();
    public int shellEffect=Constants.shellEffect;

    public ArtilleryUnit(int x, int y, int z, double kp, int range,String team,String type, int shells) {
        super(x, y, z, kp, range,"Blue","ARTY"); // Calls the Unit constructor
        this.voleys = shells;
        this.fireRadius = fireRadius;
        
    }

    // Assign target coordinates for indirect fire
    public void setTarget(int x, int y) {
        this.targetX = x;
        this.targetY = y;
    }
    
    @Override
    public boolean canAttack(Unit target) {
        double distance = Math.sqrt(Math.pow(this.x - target.x, 2) + Math.pow(this.y - target.y, 2));
        if (distance >= this.range) {
            return true;
        }
        else{
            System.out.println("Can't attack:"+target.x+"-"+ target.y);
            return false; // Ensure there's always a return value
        }

    }
    
    @Override
    public void drawUnit(int x, int y, Graphics g2, Color color){

        g2.setColor(color); // Fill color for friendly unit
        g2.fillRect(x, y, UNIT_L, UNIT_W);  // Fill the rectangle (unit frame)

        g2.setColor(Color.BLACK); // Outline color for friendly unit
        g2.drawRect(x, y, UNIT_L, UNIT_W);  // Draw the outline of the rectangle

        g2.setColor(Color.BLACK); // Cross for infantry symbol
        int circleRadius = 3;
        

        int ellipseX = x + (UNIT_L - circleRadius) / 2;
        int ellipseY = y + (UNIT_W - circleRadius) / 2;

        // Draw ellipse
        g2.setColor(Color.BLACK);
        g2.fillOval(ellipseX, ellipseY, circleRadius, circleRadius);

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        //g2.drawString("Friendly Infantry", 55, 100); 
    }
    
    public List<Unit> getDestroyedTargets(){
        return destroyed;
    }
    

    // Fire volley at the target
    public ArrayList<Unit.Point> fireVolley(List<Unit> targets) {
    impactPoints.clear();
    Random rand = new Random();
    

    for (int i = 0; i <= voleys; i++) {
        double angle = 2 * Math.PI * rand.nextDouble();
        double deviation = fireRadius * Math.sqrt(rand.nextDouble());

        int impactX = (int) (targetX + deviation *0.5* Math.cos(angle));
        int impactY = (int) (targetY + deviation *0.7* Math.sin(angle));

        impactPoints.add(new Point(impactX, impactY));

        // Check for destroyed units
        for (Unit target : targets) {
            double distToImpact = Math.sqrt(Math.pow(target.x - impactX, 2) + Math.pow(target.y - impactY, 2));
            if (distToImpact <= shellEffect) { // Within destruction range (impact radius)
                destroyed.add(target);
                System.out.println(target + " destroyed!");
            }
        }
        
    }

    // Remove destroyed units from the list
    targets.removeAll(destroyed);

    return impactPoints;
}

}
