
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;

public class ImageDistanceTool extends JPanel {

    BufferedImage image;
    Point p1 = null, p2 = null;

    public ImageDistanceTool(String imagePath) throws Exception {
        image = ImageIO.read(new File(imagePath));

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (p1 == null) {
                    p1 = e.getPoint();
                } else if (p2 == null) {
                    p2 = e.getPoint();
                    repaint();
                    double dx = p2.x - p1.x;
                    double dy = p2.y - p1.y;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    System.out.println("Distance in pixels: " + distance);
                } else {
                    p1 = e.getPoint();
                    p2 = null;
                    repaint();
                }
            }
        });
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
        g.setColor(Color.RED);
        if (p1 != null) g.fillOval(p1.x - 3, p1.y - 3, 6, 6);
        if (p2 != null) g.fillOval(p2.x - 3, p2.y - 3, 6, 6);
        if (p1 != null && p2 != null)
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(), image.getHeight());
    }

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame("Pixel Distance Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new ImageDistanceTool("C:\\Users\\danie\\Desktop\\comsim\\map.png")); // ← your image
        frame.pack();
        frame.setVisible(true);
    }
}
