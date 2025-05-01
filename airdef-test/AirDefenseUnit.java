import java.awt.*;
import javax.swing.*;

public class AirDefenseUnit  {
    public int UNIT_L=40;
    public int UNIT_W=20;
    int x;
    int y;
    
    
    public AirDefenseUnit(int x, int y,double z, double killProbability, double range, String team, String type){
         
    }
    
    // @Override
    // public boolean canAttack(Unit target){
        
        // int dx = target.x - this.x;
        // int dy = target.y - this.y;

        // double horizontalDistance = Math.sqrt(dx * dx + dy * dy);
        // double totalDistance = Math.sqrt(horizontalDistance * horizontalDistance + target.z * target.z);

        // return target.z > 0 && totalDistance <= this.range;
        
    // }
    
    
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
    
    public static void main(String[] args) {
    JFrame frame = new JFrame("Air Defense Unit Test");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 300);
    
    JPanel panel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Color lightBlue = new Color(173, 216, 230);
            AirDefenseUnit unit = new AirDefenseUnit(100, 100, 0, 0.8, 200, "Blue", "AA");
            unit.drawUnit(100, 100, g, lightBlue);
        }
    };
    
    frame.add(panel);
    frame.setVisible(true);
}



}
