 
import java.awt.*;
import javax.swing.*;

public class CASUnit extends Unit {
    public  CASUnit(int x, int y,int z, double killProbability, double range, String team, String type){
        super(x, y,z, killProbability, range, team, type);
        this.z=20;
    }
    
    @Override
    public boolean canAttack(Unit target){
        
        int dx = target.x - this.x;
        int dy = target.y - this.y;

        double horizontalDistance = Math.sqrt(dx * dx + dy * dy);
        double totalDistance = Math.sqrt(horizontalDistance * horizontalDistance + target.z * target.z);

        return target.z > 0 && totalDistance <= this.range;
        
    }

    @Override
    public void drawUnit(int rx, int ry, Graphics g2, Color color){

        g2.setColor(color); // Fill color for friendly unit
        g2.fillRect(rx, ry, UNIT_L, UNIT_W);  // Fill the rectangle (unit frame)

        g2.setColor(Color.BLACK); // Outline color for friendly unit
        g2.drawRect(rx, ry, UNIT_L, UNIT_W);  // Draw the outline of the rectangle

        g2.drawLine(rx+3, ry+3, rx+UNIT_L/2, ry+UNIT_W/2);
        g2.drawLine(rx+3, ry+6, rx+UNIT_L/2, ry+UNIT_W/2);

        g2.drawLine(rx+UNIT_L/2, ry+UNIT_W/2,rx+UNIT_L-3, ry+3);
        g2.drawLine(rx+UNIT_L/2, ry+UNIT_W/2,rx+UNIT_L-3, ry+6);
        g2.drawLine(rx+UNIT_L-3, ry+3,rx+UNIT_L-3, ry+6);
        g2.drawLine(rx+3, ry+3, rx+3, ry+6);

    }
}
