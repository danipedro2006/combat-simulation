import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Map;
import java.util.List;

public class CombatSimulator extends JPanel {
    
    private  int GRID_SIZE = Terrain.GRID_SIZE;
    private  int MAP_SIZE = Terrain.MAP_SIZE;
    public  int CELL_SIZE =Terrain.CELL_SIZE;
    private  final int MAX_ALTITUDE =Terrain.MAX_ALTITUDE;
    private  double[][] altitudeGrid =Terrain.altitudeGrid;
    
    public BattleManager battleManager;
    
    public String[] unitTypes = { "INF", "TKS", "ARTY" };
    private ArrayList<Unit> redUnits = new ArrayList<>();
    private ArrayList<Unit> blueUnits = new ArrayList<>();
    private Map<Unit, Unit> attackpairs;
    private String selectedTeam = "Red"; // Default selection
    private ArtilleryUnit selectedArtillery = null;
    private ArrayList<Unit.Point> impactPoints = new ArrayList<>();
    private Unit pathTargetUnit = null; // the unit for which you are setting a path
    
    private int atx;
    private int aty;
    
    public  boolean showAllUnits = false;
    private boolean isPathDrawing = false;
    private  boolean[][] obstructingCells =Terrain.obstructingCells;

    private BattleReportWindow reportWindow = new BattleReportWindow("Battle Reports");
    private JComboBox<String> typeSelector;
    

    public CombatSimulator() {
        this.setPreferredSize(new Dimension(MAP_SIZE, MAP_SIZE));
        this.battleManager = new BattleManager(redUnits, blueUnits);
        this.setFocusable(true);
        this.requestFocusInWindow(); // works if component is already shown

        this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX() / CELL_SIZE;
                    int y = e.getY() / CELL_SIZE;
                    int z = (int) Terrain.getAltitude(x, y);

                    String selectedType = (String) typeSelector.getSelectedItem();

                    if (isPathDrawing) {

                        if (pathTargetUnit != null) {
                            pathTargetUnit.addPathPoint(new Unit.Point(x, y));
                            System.out.println("Add path point");
                            repaint();
                        }
                        return;
                    }

                    if (SwingUtilities.isRightMouseButton(e)) {
                        Unit clickedUnit = getUnitAt(x, y);

                        if (clickedUnit != null) {
                            if (clickedUnit instanceof ArtilleryUnit && e.getClickCount() == 1) {
                                selectedArtillery = (ArtilleryUnit) clickedUnit;
                                System.out.println("Artillery selected at (" + x + ", " + y + ")");
                                JOptionPane.showMessageDialog(null, "Artillery selected at (" + x + ", " + y + ")");

                                return;
                            }
                            else{
                                JOptionPane.showMessageDialog(null, "Draw path activated");

                                startPathDrawing(clickedUnit);
                                System.out.println("Path mode started.");
                            }
                            repaint();
                        } 

                        return;
                    }

                    // Step 2: Double right-click somewhere to assign target
                    if (selectedArtillery != null && e.getClickCount() == 1) {
                        atx=x;
                        aty=y;
                        selectedArtillery.setTarget(x, y);
                        System.out.println("Target set at (" + x + ", " + y + ") for selected artillery.");
                        JOptionPane.showMessageDialog(null, "Target set at (" + x + ", " + y + ")");
                        repaint();
                        return;
                    }

                    // Default left-click: create new unit
                    Unit newUnit = UnitFactory.createUnit(selectedTeam, selectedType, x, y, z);
                    if ("Red".equals(selectedTeam)) {
                        redUnits.add(newUnit);
                    } else {
                        blueUnits.add(newUnit);
                    }
                    repaint();
                    requestFocusInWindow();

                }
            });

        this.setFocusable(true);  // Make sure the component can receive keyboard events
        this.requestFocusInWindow();

        this.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        stopPathDrawing();
                        repaint();
                    }
                }
            });

        setupui();

    }

    public void startPathDrawing(Unit unit) {
        isPathDrawing = true;
        pathTargetUnit = unit;
        //unit.clearPath(); // optional
    }

    public void stopPathDrawing() {
        isPathDrawing = false;
        pathTargetUnit = null;
    }

    private Unit getUnitAt(int x, int y) {
        for (Unit u : redUnits) {
            if (u.x == x && u.y == y) return u;
        }
        for (Unit u : blueUnits) {
            if (u.x == x && u.y == y) return u;
        }
        return null;
    }

    public List<Unit> getEnemiesInRadius(int centerX, int centerY, int radius, ArrayList<Unit> redUnits) {
        ArrayList<Unit> inRange = new ArrayList<>();

        for (Unit enemy : redUnits) {
            int ex = enemy.x; // assume you have getters
            int ey = enemy.y;

            double dist = Math.sqrt(Math.pow(ex - centerX, 2) + Math.pow(ey - centerY, 2));
            if (dist <= radius) {
                inRange.add(enemy);
            }
        }

        return inRange;
    }

    

    private void setupui(){
        JToolBar toolBar = new JToolBar();
        JButton redButton = createColorButton(Color.RED, "Red");
        JButton blueButton = createColorButton(Color.BLUE, "Blue");
        toolBar.add(redButton);
        toolBar.add(blueButton);
        typeSelector = new JComboBox<>(unitTypes);

        JButton battleButton = new JButton("Start Battle");
        JButton clearButton = new JButton("ARTY OF");

        clearButton.addActionListener(e -> {

                    new Thread(() -> {

                                selectedArtillery=null;

                                // Make sure repaint runs on the EDT
                                SwingUtilities.invokeLater(this::repaint);
                        }).start();

            });

        battleButton.addActionListener(e -> {

                    new Thread(() -> {
                                reportWindow.updateReport(battleManager.getInitialForceRatio());

                                battleManager.updateVisibility();
                                battleManager.moveUnits();
                                battleManager.findTargets();
                                battleManager.simulateCombat();

                                stopPathDrawing();

                                // Make sure repaint runs on the EDT
                                SwingUtilities.invokeLater(this::repaint);
                        }).start();

            });

        JButton fireButton = new JButton("Fire");

        fireButton.addActionListener(e -> {
                    new Thread(() -> {

                                if (selectedArtillery == null) {
                                    JOptionPane.showMessageDialog(this, "No artillery selected!", "Error", JOptionPane.ERROR_MESSAGE);
                                    return;
                                }

                                List<Unit> artytargets = getEnemiesInRadius(atx, aty, 100, redUnits);
                                impactPoints = selectedArtillery.fireVolley(artytargets);
                                battleManager.clearDestroyedTargets();

                                // Make sure repaint runs on the EDT
                                SwingUtilities.invokeLater(this::repaint);
                        }).start();

            });

        JFrame frame = new JFrame("Combat Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(this, BorderLayout.CENTER);
        JToggleButton toggleVisibility = new JToggleButton("Show All Units");

        toggleVisibility.addActionListener(e -> {
                    showAllUnits = toggleVisibility.isSelected();
                    repaint(); // or panel.repaint(), depending on your drawing surface
            });

        JButton showStatButton = new JButton("STAT");
        showStatButton.addActionListener(e -> {

                    // Show and update the report window
                    if (!reportWindow.isVisible()) {
                        reportWindow.setVisible(true);
                    }
                    String report = battleManager.getInitialForceRatio() + "\n" + battleManager.getLossesReport();
                    reportWindow.updateReport(report);

                    reportWindow.updateReport(
                        battleManager.getInitialForceRatio() + "\n" +
                        battleManager.getLossesReport()
                    );

                    repaint(); // if you want the map to refresh visuals
            });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(battleButton);
        bottomPanel.add(fireButton);
        bottomPanel.add(clearButton);
        bottomPanel.add(new JLabel("Unit Type:"));
        bottomPanel.add(typeSelector);
        bottomPanel.add(toggleVisibility);
        bottomPanel.add(showStatButton);

        // Add the bottom panel to the frame
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        //--------------------------------
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
                  try
                  {
                      ScenarioCSVExporter.exportUnitsToCSV(battleManager, "scenario.csv");
                  }
                  catch (java.io.IOException ioe)
                  {
                      ioe.printStackTrace();
                  }
                     
            });
            
        // TOOLS menu
        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem resetMapItem = new JMenuItem("Reset Map");
        resetMapItem.addActionListener(e -> {
                    // Add logic to reset map or units
                    redUnits.clear();
                    blueUnits.clear();
                    impactPoints.clear();
                    repaint();
            });
        toolsMenu.add(resetMapItem);

        // OPTIONS menu
        JMenu optionsMenu = new JMenu("Options");
        JCheckBoxMenuItem showAllUnitsItem = new JCheckBoxMenuItem("Show All Units");
        showAllUnitsItem.setSelected(showAllUnits);
        showAllUnitsItem.addActionListener(e -> {
                    showAllUnits = showAllUnitsItem.isSelected();
                    repaint();
            });
        optionsMenu.add(showAllUnitsItem);
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(optionsMenu);
       // Attach to frame
        frame.setJMenuBar(menuBar);
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
        button.setBorderPainted(true);
        button.setContentAreaFilled(true);
        button.addActionListener(e -> selectedTeam = team);
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image  backgroundImage= new ImageIcon("C:\\Users\\danie\\Desktop\\comsim\\map.png").getImage();
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        Random rand = new Random();

        if (showAllUnits) {
            for (Unit red : redUnits) {
                red.visible = true;
            }
            for (Unit blue : blueUnits) {
                blue.visible = true;
            }
            // Now draw only once
            MapRenderer.drawUnits(g, redUnits, Color.RED, CELL_SIZE);
            MapRenderer.drawUnits(g, blueUnits, Color.BLUE, CELL_SIZE);
        }

        if (!showAllUnits) {
            battleManager.updateVisibility(); // Your logic that sets unit.visible = true/false
            MapRenderer.drawUnits(g, redUnits, Color.RED, CELL_SIZE);
            MapRenderer.drawUnits(g, blueUnits, Color.BLUE, CELL_SIZE);
        }

        MapRenderer.drawPath(g,blueUnits, this.CELL_SIZE);
        attackpairs=battleManager.getAttackPairs();
        MapRenderer.drawAtack(g,attackpairs,Color.RED,CELL_SIZE);
        MapRenderer.drawImpactPoints(g,impactPoints, CELL_SIZE);

    }

    private void showUnitProperties(int x, int y) {
        new Thread(() -> {
                    Unit foundUnit = null;

                    // Heavy search logic in background
                    for (Unit unit : redUnits) {
                        if (unit.x == x && unit.y == y) {
                            foundUnit = unit;
                            break;
                        }
                    }

                    if (foundUnit == null) {
                        for (Unit unit : blueUnits) {
                            if (unit.x == x && unit.y == y) {
                                foundUnit = unit;
                                break;
                            }
                        }
                    }

                    // Show message dialog on the Event Dispatch Thread
                    if (foundUnit != null) {
                        Unit finalUnit = foundUnit;
                        SwingUtilities.invokeLater(() -> showMessage(finalUnit));
                    }
            }).start();
    }

    private void showMessage(Unit unit) {
        String message = "Unit Type: " + unit.type +
            "\nCoordinates: (" + unit.x + ", " + unit.y +", "+unit.z+ ")" +
            "\nKill Probability: " + String.format("%.2f", unit.killProbability) +
            "\nWeapon Range: " + unit.range;
        JOptionPane.showMessageDialog(this, message, "Unit Properties", JOptionPane.INFORMATION_MESSAGE);
    }
    
      
    
    public static void main(String[] args) {
        Terrain.loadTerrainElevation();

        SwingUtilities.invokeLater(CombatSimulator::new);
    }

}
