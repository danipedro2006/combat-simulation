import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DirectionPanel extends JPanel {

    private double angle = 0; // in degrees (0 is North)
    private int radius;
    private int centerX, centerY;

    public DirectionPanel() {
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateAngleFromMouse(e.getX(), e.getY());
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                updateAngleFromMouse(e.getX(), e.getY());
            }
        });
    }

    private void updateAngleFromMouse(int mouseX, int mouseY) {
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;

        // Adjust the angle calculation to make 0° point North and increase clockwise
        angle = Math.toDegrees(Math.atan2(dx, -dy)); // Switched dx and dy, and negated dy for North at 0°
        if (angle < 0) angle += 360; // Ensure angle is always between 0 and 360 degrees
        repaint();
    }

    public double getAngle() {
        return angle;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int size = Math.min(getWidth(), getHeight());
        radius = size / 3;
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));

        // Draw outer circle
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        // Compute marker position based on angle (azimuth)
        double angleRad = Math.toRadians(angle);
        int markerX = (int)(centerX + radius * Math.sin(angleRad)); // Use sin for X (horizontal)
        int markerY = (int)(centerY - radius * Math.cos(angleRad)); // Use cos for Y (vertical)

        // Draw direction line
        g2.setColor(Color.BLUE);
        g2.drawLine(centerX, centerY, markerX, markerY);

        // Draw marker
        g2.setColor(Color.RED);
        g2.fillOval(markerX - 6, markerY - 6, 12, 12);

        // Draw angle text
        g2.setColor(Color.BLACK);
        g2.drawString(String.format("Angle: %.1f°", angle), 10, 20);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Interactive Direction Selector");
        DirectionPanel directionPanel = new DirectionPanel();
        frame.add(directionPanel);
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
