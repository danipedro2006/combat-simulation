import java.awt.*;
import javax.swing.*;

public class AirDefenseUnit extends Unit {
    
    public AirDefenseUnit(int x, int y,int z, double killProbability, double range, String team, String type){
        super(x, y,z, killProbability, range, team, type);
        this.sensorRange=60;
    }
    
    @Override
    public boolean canAttack(Unit target){
        
        int dx = target.x - this.x;
        int dy = target.y - this.y;

        double horizontalDistance = Math.sqrt(dx * dx + dy * dy);
        double totalDistance = Math.sqrt(horizontalDistance * horizontalDistance + target.z * target.z);

        return ((target.z > 0) && (totalDistance <= this.range) && (target instanceof CASUnit));
        
    }
    
    @Override
     public void drawUnit(int rx, int ry, Graphics g2, Color color){

        g2.setColor(color); // Fill color for friendly unit
        g2.fillRect(rx, ry, UNIT_L, UNIT_W);  // Fill the rectangle (unit frame)

        g2.setColor(Color.BLACK); // Outline color for friendly unit
        g2.drawRect(rx, ry, UNIT_L, UNIT_W);  // Draw the outline of the rectangle

        int centerX = rx+UNIT_L/2;
        int centerY = ry+UNIT_L/2;
        
        int radius = UNIT_W;

        // x, y = top-left corner of bounding rectangle
        int x = centerX - radius;
        int y = centerY - radius;

        int width = 2 * radius;
        int height = 2 * radius;

        // Start angle: 0 is at 3 o'clock, and angles increase counter-clockwise
        int startAngle = 0;
        int arcAngle = 180;

        g2.setColor(Color.BLACK);
        g2.drawArc(x, y, width, height, startAngle, arcAngle);

         
    }
    
    
}




