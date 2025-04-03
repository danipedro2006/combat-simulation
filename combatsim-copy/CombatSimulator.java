import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CombatSimulator extends JPanel {
    private static final int GRID_SIZE = 100;
    private static final int MAP_SIZE = 1000;
    private static final int CELL_SIZE = MAP_SIZE / GRID_SIZE;

    private ArrayList<Unit> redUnits = new ArrayList<>();
    private ArrayList<Unit> blueUnits = new ArrayList<>();
    private BattleManager battleManager;
    private String selectedTeam = "Red"; // Default selection

    public CombatSimulator() {
        this.setPreferredSize(new Dimension(MAP_SIZE, MAP_SIZE));
        battleManager = new BattleManager(redUnits, blueUnits);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / CELL_SIZE;
                int y = e.getY() / CELL_SIZE;

                if (SwingUtilities.isRightMouseButton(e)) {
                    showUnitProperties(x, y);
                } else {
                    double killProb = 0.5;
                    double range = 10;

                    if ("Red".equals(selectedTeam)) {
                        redUnits.add(new Unit(x, y, killProb, range, "Red"));
                    } else {
                        blueUnits.add(new Unit(x, y, killProb, range, "Blue"));
                    }
                    repaint();
                }
            }
        });

        // Toolbar for selecting unit color
        JToolBar toolBar = new JToolBar();
        JButton redButton = createColorButton(Color.RED, "Red");
        JButton blueButton = createColorButton(Color.BLUE, "Blue");
        toolBar.add(redButton);
        toolBar.add(blueButton);
        
        JButton battleButton = new JButton("Start Battle");
        battleButton.addActionListener(e -> {
            battleManager.findTargets();
            battleManager.simulateCombat();
            repaint();
        });
        
        JFrame frame = new JFrame("Combat Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(this, BorderLayout.CENTER);
        frame.add(battleButton, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton createColorButton(Color color, String team) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(color);
                g.fillOval(5, 5, 20, 20);
            }
        };
        button.setPreferredSize(new Dimension(30, 30));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(e -> selectedTeam = team);
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i <= GRID_SIZE; i++) {
            int pos = i * CELL_SIZE;
            g.drawLine(pos, 0, pos, MAP_SIZE);
            g.drawLine(0, pos, MAP_SIZE, pos);
        }
        drawUnits(g, redUnits, Color.RED);
        drawUnits(g, blueUnits, Color.BLUE);
    }

    private void drawUnits(Graphics g, ArrayList<Unit> units, Color color) {
        g.setColor(color);
        int index=0;
        for (Unit unit : units) {
            int x = unit.x * CELL_SIZE + CELL_SIZE / 2;
            int y = unit.y * CELL_SIZE + CELL_SIZE / 2;
            g.fillOval(x - 5, y - 5, 10, 10);
            index++;
            g.drawString(unit.type.charAt(0) + String.valueOf(index), x - 10, y - 10);
        }
    }

    private void showUnitProperties(int x, int y) {
        for (Unit unit : redUnits) {
            if (unit.x == x && unit.y == y) {
                showMessage(unit);
                return;
            }
        }
        for (Unit unit : blueUnits) {
            if (unit.x == x && unit.y == y) {
                showMessage(unit);
                return;
            }
        }
    }

    private void showMessage(Unit unit) {
        String message = "Unit Type: " + unit.type +
                "\nCoordinates: (" + unit.x + ", " + unit.y + ")" +
                "\nKill Probability: " + String.format("%.2f", unit.killProbability) +
                "\nWeapon Range: " + unit.range;
        JOptionPane.showMessageDialog(this, message, "Unit Properties", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CombatSimulator::new);
    }
}
