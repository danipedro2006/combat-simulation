import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class OverlayApp extends JFrame {
    public JToolBar toolbar;

    public OverlayApp() {
        setLayout(new BorderLayout());
        this.toolbar = new JToolBar("Overlay");
        setTitle("Overlay Shapes");
        setSize(1266, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton btnLine = new JButton(new ImageIcon("line.png"));
        btnLine.addActionListener(e -> JOptionPane.showMessageDialog(this, "Calendar clicked"));
        toolbar.add(btnLine);

        DrawPanel drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);
        add(toolbar, BorderLayout.NORTH);
            


        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem loadImageItem = new JMenuItem("Load Background Image");
        loadImageItem.addActionListener(e -> drawPanel.loadBackgroundImage());

        JMenuItem exportShapesItem = new JMenuItem("Export Shapes Only (PNG)");
        exportShapesItem.addActionListener(e -> drawPanel.exportShapesOnly());

        JMenuItem loadOverlayItem = new JMenuItem("Load Shape Overlay (PNG)");
        loadOverlayItem.addActionListener(e -> drawPanel.loadOverlayImage());

        JMenuItem exportCombinedItem = new JMenuItem("Export Background + Shapes (PNG)");
        exportCombinedItem.addActionListener(e -> drawPanel.exportCombinedImage());

        JMenuItem colorPickerItem = new JMenuItem("Pick Shape Color");
        colorPickerItem.addActionListener(e -> drawPanel.pickColor());

        fileMenu.add(loadImageItem);
        fileMenu.add(loadOverlayItem);
        fileMenu.add(exportShapesItem);
        fileMenu.add(exportCombinedItem);
        fileMenu.add(colorPickerItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        setLocationRelativeTo(null);
        toolbar.add(btnLine); 
    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new OverlayApp().setVisible(true));
    }
}

class DrawPanel extends JPanel {
    private final List<ShapeSet> shapes = new ArrayList<>();
    private final List<Circle> circles = new ArrayList<>();
    private ShapeSet currentShape = new ShapeSet(Color.RED);
    private Color currentColor = Color.RED;

    private Image backgroundImage;
    private Image overlayImage;

    private Point circleStart = null;
    private Point circleEnd = null;

    public DrawPanel() {

        try {
            // Load default image from resources or project directory
            BufferedImage defaultImg = ImageIO.read(new File("C:\\Users\\danie\\Desktop\\comsim\\map.png"));
            this.backgroundImage = defaultImg;
        } catch (Exception ex) {
            System.out.println("Default image not found, starting with blank background.");
        }
        MouseAdapter mouseAdapter = new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()) {
                        circleStart = e.getPoint();
                    }
                }

                public void mouseDragged(MouseEvent e) {
                    if (circleStart != null) {
                        circleEnd = e.getPoint();
                        repaint();
                    }
                }

                public void mouseReleased(MouseEvent e) {
                    if (circleStart != null && circleEnd != null) {
                        int radius = (int) circleStart.distance(circleEnd);
                        circles.add(new Circle(circleStart, radius, currentColor));
                        circleStart = null;
                        circleEnd = null;
                        repaint();
                    }
                }

                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && !e.isShiftDown()) {
                        currentShape.points.add(e.getPoint());
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        if (currentShape.points.size() >= 2) {
                            if (currentShape.points.size() > 2) {
                                currentShape.isPolygon = true;
                            }
                            shapes.add(currentShape);
                        }
                        currentShape = new ShapeSet(currentColor);
                    }
                    repaint();
                }
            };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public void pickColor() {
        Color chosen = JColorChooser.showDialog(this, "Pick Shape Color", currentColor);
        if (chosen != null) {
            currentColor = chosen;
            currentShape.color = currentColor;
        }
    }

    public void loadBackgroundImage() {

        //backgroundImage=new ImageIcon("C://Users//danie//Desktop//comsim//map.png").getImage();
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                backgroundImage = ImageIO.read(chooser.getSelectedFile());
                repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load background image.");
            }
        }
    }

    public void loadOverlayImage() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                overlayImage = ImageIO.read(chooser.getSelectedFile());
                repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load overlay image.");
            }
        }
    }

    public void exportShapesOnly() {
        if (backgroundImage == null) {
            JOptionPane.showMessageDialog(this, "Load background first to get size.");
            return;
        }
        int width = backgroundImage.getWidth(null);
        int height = backgroundImage.getHeight(null);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.scale((double) width / getWidth(), (double) height / getHeight());
        paintShapesOnly(g2);
        g2.dispose();
        saveImage(img);
    }

    public void exportCombinedImage() {
        BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        paintAll(g2);
        g2.dispose();
        saveImage(img);
    }

    private void saveImage(BufferedImage img) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ImageIO.write(img, "png", chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Image saved.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save image.");
            }
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (backgroundImage != null) {
            //g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2.drawImage(backgroundImage, 0, 0, 1200, 600, this);
        }
        paintShapesOnly(g2);
        if (overlayImage != null) {
            g2.drawImage(overlayImage, 0, 0, getWidth(), getHeight(), this);
        }
        if (circleStart != null && circleEnd != null) {
            int radius = (int) circleStart.distance(circleEnd);
            g2.setColor(currentColor);
            g2.drawOval(circleStart.x - radius, circleStart.y - radius, radius * 2, radius * 2);
        }
    }

    private void paintShapesOnly(Graphics2D g2) {
        g2.setStroke(new BasicStroke(2));
        for (ShapeSet s : shapes) {
            drawShape(g2, s);
        }
        for (Circle c : circles) {
            g2.setColor(c.color);
            g2.drawOval(c.center.x - c.radius, c.center.y - c.radius, c.radius * 2, c.radius * 2);
        }
        drawShape(g2, currentShape);
    }

    private void drawShape(Graphics2D g2, ShapeSet s) {

        g2.setColor(s.color);
        if (s.points.size() == 2) {
            g2.drawLine(s.points.get(0).x, s.points.get(0).y, s.points.get(1).x, s.points.get(1).y);
        } else if (s.points.size() > 2) {
            int[] x = s.points.stream().mapToInt(p -> p.x).toArray();
            int[] y = s.points.stream().mapToInt(p -> p.y).toArray();
            if (s.isPolygon) {
                g2.drawPolygon(x, y, s.points.size());
            } else {
                for (int i = 0; i < s.points.size() - 1; i++) {
                    g2.drawLine(s.points.get(i).x, s.points.get(i).y, s.points.get(i + 1).x, s.points.get(i + 1).y);
                }
            }
        }
    }
}

class ShapeSet {
    List<Point> points = new ArrayList<>();
    boolean isPolygon = false;
    Color color;
    ShapeSet(Color c) { this.color = c; }
}

class Circle {
    Point center;
    int radius;
    Color color;
    Circle(Point c, int r, Color col) {
        center = c;
        radius = r;
        color = col;
    }
}
