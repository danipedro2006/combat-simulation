
import javax.swing.*;
import java.awt.*;

public class TransparentDemo extends JFrame {
    public TransparentDemo() {
        setTitle("Transparent Layout Demo");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Custom panel with background image
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // Transparent top panel with buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Make it transparent
        buttonPanel.setLayout(new FlowLayout());

        JButton btn1 = new JButton("Open Panel 1");
        JButton btn2 = new JButton("Open Panel 2");

        buttonPanel.add(btn1);
        buttonPanel.add(btn2);

        // Add button panel on top of background
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TransparentDemo());
    }
}

// 📷 Panel that draws a background image
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel() {
        // You can load any image from your disk or resources
        backgroundImage = new ImageIcon("C:\\Users\\danie\\Desktop\\comsim\\topomap.png").getImage(); // <-- Replace this with your image path
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw the image stretched to fit the panel
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
