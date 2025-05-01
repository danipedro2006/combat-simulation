import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

public class TankUnit extends Unit implements gunnable{
    public double protection;
    public double killProbability;

    public TankUnit (int x, int y,int z, double killProbability, double range, String team, String type, double protection){
        super(x, y,z, killProbability, range, team, type);
        this.protection=protection;

    }

    @Override
    public void drawUnit(int x, int y, Graphics g2, Color color){

        g2.setColor(color); // Fill color for friendly unit
        g2.fillRect(x, y, UNIT_L, UNIT_W);  // Fill the rectangle (unit frame)

        g2.setColor(Color.BLACK); // Outline color for friendly unit
        g2.drawRect(x, y, UNIT_L, UNIT_W);  // Draw the outline of the rectangle

        g2.setColor(Color.BLACK); // Cross for infantry symbol
        int ellipseWidth = 16;
        int ellipseHeight = 6;

        int ellipseX = x + (UNIT_L - ellipseWidth) / 2;
        int ellipseY = y + (UNIT_W - ellipseHeight) / 2;

        // Draw ellipse
        g2.setColor(Color.BLACK);
        g2.drawOval(ellipseX, ellipseY, ellipseWidth, ellipseHeight);

        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Friendly Infantry", 55, 100); 
    }

    @Override
    public void fire(Unit target){
        double distance = Math.sqrt(Math.pow(this.x - target.x, 2) + Math.pow(this.y - target.y, 2));

        // Kill probability decreases with distance (min 0.3)
        this.killProbability = Math.max(0.3, 1.0 - (distance / 100.0));
        System.out.println("Target destroyed with probability:"+this.killProbability);

    }
    

}
