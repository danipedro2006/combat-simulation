 

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Map;
import java.util.List;

class UIManager extends JPanel {

	private CombatSimulator simulator;

	private BattleReportWindow reportWindow = new BattleReportWindow("Battle Reports");

	public boolean showAllUnits = false;

	private String selectedTeam = "Red"; // Default selection

	public UIManager(CombatSimulator simulator, BattleReportWindow battlereportwindow) {

		this.simulator = simulator;
		this.reportWindow = battlereportwindow;
		this.setPreferredSize(new Dimension(simulator.MAP_SIZE, simulator.MAP_SIZE));

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX() / simulator.CELL_SIZE;
				int y = e.getY() / simulator.CELL_SIZE;
				int z = (int) Terrain.getAltitude(x, y);

				if (simulator.isPathDrawing && SwingUtilities.isLeftMouseButton(e)) {

					if (simulator.pathTargetUnit != null) {
						simulator.pathTargetUnit.addPathPoint(new Unit.Point(x, y));
						System.out.println("Add path point");
						repaint();
					}
					//return;
				}

				else if (SwingUtilities.isLeftMouseButton(e)) {
					Unit clickedUnit = simulator.getUnitAt(x, y);

					if (clickedUnit != null) {
						if (clickedUnit instanceof ArtilleryUnit && e.getClickCount() == 1) {
							simulator.selectedArtillery = (ArtilleryUnit) clickedUnit;
							System.out.println("Artillery selected at (" + x + ", " + y + ")");
							JOptionPane.showMessageDialog(null, "Artillery selected at (" + x + ", " + y + ")");

							return;
						} else {
							JOptionPane.showMessageDialog(null, "Draw path activated");

							simulator.startPathDrawing(clickedUnit);
							System.out.println("Path mode started.");
						}
						repaint();
					}

					//return;
				}

				// Step 2: Double right-click somewhere to assign target
				else if (simulator.selectedArtillery != null && e.getClickCount() == 1 && simulator.isTargeting==true) {
					simulator.artileryTgX = x;
					simulator.artileryTgY = y;
					simulator.selectedArtillery.setTarget(x, y);
					System.out.println("Target set at (" + x + ", " + y + ") for selected artillery.");
					JOptionPane.showMessageDialog(null, "Target set at (" + x + ", " + y + ")");
					repaint();
					//return;
				}
				else if (SwingUtilities.isRightMouseButton(e)) {
					Unit clickedUnit = simulator.getUnitAt(x, y);
					if (clickedUnit != null) {
						String text=clickedUnit.toString();
						JOptionPane.showMessageDialog(null, text);
					}
					
				}
				
				
				

				repaint();
				requestFocusInWindow();

			}
		});

		this.setFocusable(true); // Make sure the component can receive keyboard events
		this.requestFocusInWindow();

		

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

	public void setupUI() {

		JMenuBar menuBar = new JMenuBar();
		JButton battleButton = new JButton("Start");
		JButton clearButton = new JButton("Fire/OF");
		JButton fireButton = new JButton("Fire/ON");
		JButton setupButton = new JButton("SetUp");
		JToggleButton togglePathButton = new JToggleButton("P/OFF");
		JToggleButton toggleTgtButton = new JToggleButton("T/OFF");
		
		toggleTgtButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (toggleTgtButton.isSelected()) {
					toggleTgtButton.setText("T/ON");
					simulator.isTargeting = true;
					System.out.println(" TGTButton is ON");
				} else {
					toggleTgtButton.setText("T/OFF");
					simulator.isTargeting = false;
					System.out.println("TGT Button is OFF");
				}
			}
		});

		togglePathButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (togglePathButton.isSelected()) {
					togglePathButton.setText("P/ON");
					simulator.isPathDrawing = true;
					System.out.println("Path Button is ON");
				} else {
					togglePathButton.setText("P/OFF");
					simulator.isPathDrawing = false;
					System.out.println("Path Button is OFF");
				}
			}
		});

		setupButton.addActionListener(e -> {
			UnitSetupWindow setupWindow = new UnitSetupWindow(simulator);
			repaint();
			setupWindow.setVisible(true);
		});

		clearButton.addActionListener(e -> {
			new Thread(() -> {
				simulator.selectedArtillery = null;
				// Make sure repaint runs on the EDT
				SwingUtilities.invokeLater(this::repaint);
			}).start();

		});

		battleButton.addActionListener(e -> {

			new Thread(() -> {
				simulator.stopPathDrawing();
				reportWindow.updateReport(simulator.getInitialForceRatio());

				simulator.updateVisibility();
				simulator.moveUnits();
				simulator.findTargets();
				simulator.simulateCombat();
				// Make sure repaint runs on the EDT
				SwingUtilities.invokeLater(this::repaint);
			}).start();

		});

		fireButton.addActionListener(e -> {
			new Thread(() -> {

				if (simulator.selectedArtillery == null) {
					JOptionPane.showMessageDialog(this, "No artillery selected!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				List<Unit> artytargets = simulator.getEnemiesInRadius(simulator.artileryTgX, simulator.artileryTgY, 100,
						simulator.redUnits);
				simulator.impactPoints = simulator.selectedArtillery.fireVolley(artytargets);
				simulator.clearDestroyedTargets();

				// Make sure repaint runs on the EDT
				SwingUtilities.invokeLater(this::repaint);
			}).start();

		});

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottomPanel.add(setupButton);
		bottomPanel.add(battleButton);
		bottomPanel.add(fireButton);
		bottomPanel.add(toggleTgtButton);
		bottomPanel.add(togglePathButton);

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

		newItem.addActionListener(e -> {

			UnitSetupWindow setupWindow = new UnitSetupWindow(simulator);
			repaint();
			setupWindow.setVisible(true);

		});

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
		JMenuItem showStatItem = new JMenuItem("Show Stat");

		resetMapItem.addActionListener(e -> {

			simulator.redUnits.clear();
			simulator.blueUnits.clear();
			simulator.impactPoints.clear();
			repaint();
		});

		showStatItem.addActionListener(e -> {

			if (!reportWindow.isVisible()) {
				reportWindow.setVisible(true);
			}
			String report = simulator.getInitialForceRatio() + "\n" + simulator.getLossesReport();
			reportWindow.updateReport(report);

			reportWindow.updateReport(simulator.getInitialForceRatio() + "\n" + simulator.getLossesReport());

			repaint(); // if you want the map to refresh visuals

		});

		toolsMenu.add(resetMapItem);
		toolsMenu.add(showStatItem);

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

		showAllUnitsItem.setSelected(simulator.isShowAllUnits);
		showAllUnitsItem.addActionListener(e -> {
			simulator.isShowAllUnits = showAllUnitsItem.isSelected();
			repaint();
		});

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

		JFrame frame = new JFrame("Combat Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1266, 620));
		frame.setLayout(new BorderLayout());
		frame.add(this, BorderLayout.CENTER);
		frame.add(bottomPanel, BorderLayout.SOUTH);

		frame.setJMenuBar(menuBar);

		// Add the bottom panel to the frame

		frame.pack();
		frame.setLocationRelativeTo(null);

		// --------------------------------

		frame.setVisible(true);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Terrain.drawMap(g, getWidth(), getHeight());
		Random rand = new Random();

		if (simulator.isShowAllUnits) {
			for (Unit red : simulator.redUnits) {
				red.visible = true;
			}
			for (Unit blue : simulator.blueUnits) {
				blue.visible = true;
			}
			// Now draw only once
			MapRenderer.drawUnits(g, simulator.redUnits, Color.RED, simulator.CELL_SIZE);
			MapRenderer.drawUnits(g, simulator.blueUnits, Color.BLUE, simulator.CELL_SIZE);
		}

		if (!simulator.isShowAllUnits) {
			simulator.updateVisibility(); // Your logic that sets unit.visible = true/false
			MapRenderer.drawUnits(g, simulator.redUnits, Color.RED, simulator.CELL_SIZE);
			MapRenderer.drawUnits(g, simulator.blueUnits, Color.BLUE, simulator.CELL_SIZE);
		}

		MapRenderer.drawPath(g, simulator.blueUnits, simulator.CELL_SIZE);
		simulator.attackpairs = simulator.getAttackPairs(simulator);
		MapRenderer.drawAtack(g, simulator.attackpairs, Color.RED, simulator.CELL_SIZE);
		MapRenderer.drawImpactPoints(g, simulator.impactPoints, simulator.CELL_SIZE);

	}

}
