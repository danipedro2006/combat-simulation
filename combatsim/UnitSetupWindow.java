 

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

public class UnitSetupWindow extends JPanel {
    private CombatSimulator simulator;
     
    JFrame frame;
    public boolean showAllUnits = true;
    public String[] unitTypes = { "INF", "TKS", "ARTY", "AD", "CAS" };

    private JComboBox<String> typeSelector;
    private String selectedTeam = "Red"; // Default selection

    public UnitSetupWindow(CombatSimulator simulator) {
        this.simulator = simulator;
         
        this.setPreferredSize(new Dimension(simulator.MAP_SIZE, simulator.MAP_SIZE));///Atentie care e marimea
        
        setupUI();
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / simulator.CELL_SIZE;
                int y = e.getY() / simulator.CELL_SIZE;
                int z = (int) Terrain.getAltitude(x, y);

                String selectedType = (String) typeSelector.getSelectedItem();

                Unit newUnit = UnitFactory.createUnit(selectedTeam, selectedType, x, y, z);
                if ("Red".equals(selectedTeam)) {
                    simulator.redUnits.add(newUnit);
                } else {
                    simulator.blueUnits.add(newUnit);
                }
                repaint();
                requestFocusInWindow();

            }
        });

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
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.addActionListener(e -> selectedTeam = team);
        return button;
    }

    private void setupUI() {
        

        JToolBar toolBar = new JToolBar();
        JButton redButton = createColorButton(Color.RED, "Red");
        JButton blueButton = createColorButton(Color.BLUE, "Blue");
        toolBar.add(redButton);
        toolBar.add(blueButton);
        typeSelector = new JComboBox<>(unitTypes);

        JButton finishButton = new JButton("Finish");

        finishButton.addActionListener(e -> {
            this.frame.dispose();  // closes the setup window when Finish is clicked
        });

        this.frame = new JFrame("SetUpUnits");
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1266, 480));
        frame.setLayout(new BorderLayout());
        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(this, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(finishButton);

        bottomPanel.add(new JLabel("Unit Type:"));
        bottomPanel.add(typeSelector);
        // bottomPanel.add(showStatButton);

        // Add the bottom panel to the frame
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);

        // --------------------------------
        JMenu optionsMenu = new JMenu("Options");
        JCheckBoxMenuItem showAllUnitsItem = new JCheckBoxMenuItem("Show All Units");
        showAllUnitsItem.setSelected(simulator.isShowAllUnits);
        showAllUnitsItem.addActionListener(e -> {
            simulator.isShowAllUnits = showAllUnitsItem.isSelected();
            repaint();
        });

        JMenuBar menuBar = new JMenuBar();
        // FILE menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        JMenuItem saveItem = new JMenuItem("Save scenario");
        JMenuItem loadItem = new JMenuItem("Load scenario");
        JMenuItem newItem = new JMenuItem("New");
        fileMenu.add(newItem);
        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        exitItem.addActionListener(e -> System.exit(0));

        saveItem.addActionListener(e -> {
            try {
                ScenarioCSVExporter.exportUnitsToCSV(simulator, "scenario.csv");
            } catch (java.io.IOException ioe) {
                ioe.printStackTrace();
            }

        });

        loadItem.addActionListener(e -> {

            System.out.println("Called scenario import method in combat simulator");
            try {
                simulator.loadUnitsFromCSV("scenario.csv");
            } catch (java.io.IOException ioe) {
                ioe.printStackTrace();
            }

        });

        // TOOLS menu
        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem resetMapItem = new JMenuItem("Reset Map");
        JMenuItem showStatistics = new JMenuItem("Show stat");

        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Terrain.drawMap(g, getWidth(), getHeight());

        for (Unit red : simulator.redUnits) {
            red.visible = true;
        }

        for (Unit blue : simulator.blueUnits) {
            blue.visible = true;
        }

        MapRenderer.drawUnits(g, simulator.redUnits, Color.RED, simulator.CELL_SIZE);
        MapRenderer.drawUnits(g, simulator.blueUnits, Color.BLUE, simulator.CELL_SIZE);
    }
}
