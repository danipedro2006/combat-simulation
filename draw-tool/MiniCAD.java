import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MiniCAD extends JFrame {
    enum Tool { LINE, POLYLINE, CIRCLE }
    private Tool currentTool = Tool.LINE;

    public MiniCAD() {
        setTitle("Mini CAD App");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        CanvasPanel canvas = new CanvasPanel();
        JPanel buttonPanel = new JPanel();

        JButton lineBtn = new JButton("Line");
        JButton polylineBtn = new JButton("Polyline");
        JButton circleBtn = new JButton("Circle");

        lineBtn.addActionListener(e -> {
            currentTool = Tool.LINE;
            canvas.setTool(currentTool);
        });

        polylineBtn.addActionListener(e -> {
            currentTool = Tool.POLYLINE;
            canvas.setTool(currentTool);
        });

        circleBtn.addActionListener(e -> {
            currentTool = Tool.CIRCLE;
            canvas.setTool(currentTool);
        });

        buttonPanel.add(lineBtn);
        buttonPanel.add(polylineBtn);
        buttonPanel.add(circleBtn);

        add(buttonPanel, BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MiniCAD::new);
    }
}

class CanvasPanel extends JPanel {
    private MiniCAD.Tool tool = MiniCAD.Tool.LINE;

    private Point startPoint = null;
    private ArrayList<Point> polylinePoints = new ArrayList<>();
    private final ArrayList<Shape> shapes = new ArrayList<>();

    public CanvasPanel() {
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                switch (tool) {
                    case LINE:
                        if (startPoint == null) {
                            startPoint = e.getPoint();
                        } else {
                            shapes.add(new Line2D(startPoint, e.getPoint()));
                            startPoint = null;
                            repaint();
                        }
                        break;

                    case CIRCLE:
                        if (startPoint == null) {
                            startPoint = e.getPoint();
                        } else {
                            shapes.add(new Circle(startPoint, e.getPoint()));
                            startPoint = null;
                            repaint();
                        }
                        break;

                    case POLYLINE:
                        if (e.getClickCount() == 2) {
                            shapes.add(new Polyline(new ArrayList<>(polylinePoints)));
                            polylinePoints.clear();
                            repaint();
                        } else {
                            polylinePoints.add(e.getPoint());
                        }
                        break;
                }
            }
        });
    }

    public void setTool(MiniCAD.Tool tool) {
        this.tool = tool;
        startPoint = null;
        polylinePoints.clear();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Shape shape : shapes) {
            shape.draw(g);
        }

        // Draw polyline preview
        if (tool == MiniCAD.Tool.POLYLINE && polylinePoints.size() > 1) {
            for (int i = 0; i < polylinePoints.size() - 1; i++) {
                Point p1 = polylinePoints.get(i);
                Point p2 = polylinePoints.get(i + 1);
                g.setColor(Color.GRAY);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }
}

// Shape interface
interface Shape {
    void draw(Graphics g);
}

// Line shape
class Line2D implements Shape {
    private final Point p1, p2;

    public Line2D(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }
}

// Circle shape
class Circle implements Shape {
    private final Point center, edge;

    public Circle(Point center, Point edge) {
        this.center = center;
        this.edge = edge;
    }

    public void draw(Graphics g) {
        int radius = (int) center.distance(edge);
        g.setColor(Color.BLUE);
        g.drawOval(center.x - radius, center.y - radius, 2 * radius, 2 * radius);
    }
}

// Polyline shape
class Polyline implements Shape {
    private final ArrayList<Point> points;

    public Polyline(ArrayList<Point> points) {
        this.points = points;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
}
