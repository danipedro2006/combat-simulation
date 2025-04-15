import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MapEditor extends JFrame {
    private MapPanel mapPanel;

    public MapEditor() {
        setTitle("Map Editor with Background and Overlays");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mapPanel = new MapPanel();
        add(new JScrollPane(mapPanel), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton loadBackgroundBtn = new JButton("Load Background");
        JButton saveElevationBtn = new JButton("Save Elevation");
        JButton regenElevationBtn = new JButton("Regenerate Relief");

        JCheckBox showBackgroundChk = new JCheckBox("Show Background", true);
        JCheckBox showGridChk = new JCheckBox("Show Grid", true);
        JCheckBox showElevationChk = new JCheckBox("Show Elevation", true);

        controlPanel.add(loadBackgroundBtn);
        controlPanel.add(saveElevationBtn);
        controlPanel.add(regenElevationBtn);
        controlPanel.add(showBackgroundChk);
        controlPanel.add(showGridChk);
        controlPanel.add(showElevationChk);

        add(controlPanel, BorderLayout.SOUTH);

        loadBackgroundBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int result = fc.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                mapPanel.loadBackgroundImage(fc.getSelectedFile().getAbsolutePath());
            }
        });

        saveElevationBtn.addActionListener(e -> {
            try {
                mapPanel.saveElevation("elevation.csv");
                JOptionPane.showMessageDialog(this, "Saved to elevation.csv");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Save failed.");
            }
        });

        regenElevationBtn.addActionListener(e -> mapPanel.generateRandomElevation());
        showBackgroundChk.addActionListener(e -> mapPanel.toggleBackground());
        showGridChk.addActionListener(e -> mapPanel.toggleGrid());
        showElevationChk.addActionListener(e -> mapPanel.toggleElevation());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MapEditor().setVisible(true);
        });
    }
}
