import javax.swing.*;
import java.awt.*;

class MilitarySymbolPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // ---- Friendly Infantry Symbol (Rectangle) ----
        g2.setColor(Color.BLUE); // Fill color for friendly unit
        g2.fillRect(50, 50, 40, 30);  // Fill the rectangle (unit frame)
        
        g2.setColor(Color.BLACK); // Outline color for friendly unit
        g2.drawRect(50, 50, 40, 30);  // Draw the outline of the rectangle
        
        g2.setColor(Color.BLACK); // Cross for infantry symbol
        g2.drawLine(50, 50, 90, 80);  // Diagonal "X" line 1
        g2.drawLine(50, 80, 90, 50);  // Diagonal "X" line 2
        
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Friendly Infantry", 55, 100);  // Label below the symbol

        // ---- Enemy Tank Symbol (Rhombus/Diamond) ----
        g2.setColor(Color.RED); // Fill color for enemy unit
        int[] xPoints = {150, 180, 150, 120}; // Points for the rhombus
        int[] yPoints = {50, 80, 110, 80};
        g2.fillPolygon(xPoints, yPoints, 4); // Fill the rhombus for the enemy tank
        
        g2.setColor(Color.BLACK); // Outline color for enemy unit
        g2.drawPolygon(xPoints, yPoints, 4); // Draw the outline of the rhombus
        
        // ---- Draw the Outlined Ellipse (symbolizing tank) ----
        g2.setColor(Color.BLACK); // Ellipse outline color
        g2.drawOval(135, 70, 30, 20); // Outlined ellipse inside the diamond representing the tank
        
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Enemy Tank", 130, 120);  // Label below the symbol
    }
}

public class MilitarySymbols {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Military Symbols");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.add(new MilitarySymbolPanel());
        frame.setVisible(true);
    }
}
