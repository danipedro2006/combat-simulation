import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Random;

public class MapPanel extends JPanel {
    private BufferedImage backgroundImage;
    private boolean showBackground = true;
    private boolean showGrid = true;
    private boolean showElevation = true;

    private float[][] elevation;
    private int rows = 60, cols = 80;
    private int cellSize = 10;

    private double zoom = 1.0;
    private int offsetX = 0, offsetY = 0;
    private Point dragStart;

    public MapPanel() {
        elevation = new float[rows][cols];
        generateRandomElevation();

        // Drag to pan
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point dragEnd = e.getPoint();
                offsetX += dragEnd.x - dragStart.x;
                offsetY += dragEnd.y - dragStart.y;
                dragStart = dragEnd;
                repaint();
            }
        });

        // Mouse wheel to zoom
        addMouseWheelListener(e -> {
            if (e.getPreciseWheelRotation() < 0) zoom *= 1.1;
            else zoom /= 1.1;
            repaint();
        });
    }

    public void loadBackgroundImage(String path) {
        try {
            backgroundImage = ImageIO.read(new File(path));
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toggleBackground() {
        showBackground = !showBackground;
        repaint();
    }

    public void toggleGrid() {
        showGrid = !showGrid;
        repaint();
    }

    public void toggleElevation() {
        showElevation = !showElevation;
        repaint();
    }

    public void generateRandomElevation() {
        Random rand = new Random();
        for (int y = 0; y < rows; y++)
            for (int x = 0; x < cols; x++)
                elevation[y][x] = rand.nextFloat() * 100;
        repaint();
    }

    public void saveElevation(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    writer.print(elevation[y][x]);
                    if (x < cols - 1) writer.print(",");
                }
                writer.println();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.translate(offsetX, offsetY);
        g2d.scale(zoom, zoom);

        // Draw background
        if (showBackground && backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, cols * cellSize, rows * cellSize, null);
        }

        // Draw elevation overlay
        if (showElevation) {
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    float height = elevation[y][x];
                    int gray = (int) (height / 100 * 255);
                    gray = Math.min(255, Math.max(0, gray));
                    g2d.setColor(new Color(gray, gray, gray, 150));
                    g2d.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                }
            }
        }

        // Draw grid
        if (showGrid) {
            g2d.setColor(Color.BLACK);
            for (int x = 0; x <= cols; x++)
                g2d.drawLine(x * cellSize, 0, x * cellSize, rows * cellSize);
            for (int y = 0; y <= rows; y++)
                g2d.drawLine(0, y * cellSize, cols * cellSize, y * cellSize);
        }

        g2d.dispose();
    }
}
