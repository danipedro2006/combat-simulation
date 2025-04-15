import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MapEditor extends JPanel {
    private static final int CELL_SIZE = 40;
    private static final int ROWS = 10;
    private static final int COLS = 15;

    private enum TileType {
        EMPTY, TERRAIN, OBSTACLE, VEGETATION
    }

    private TileType[][] mapGrid = new TileType[ROWS][COLS];
    private TileType selectedTile = TileType.TERRAIN;

    public MapEditor() {
        // Initialize grid to EMPTY
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                mapGrid[row][col] = TileType.EMPTY;
            }
        }

        // Mouse interaction for placing tiles
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = e.getX() / CELL_SIZE;
                int row = e.getY() / CELL_SIZE;
                if (row < ROWS && col < COLS) {
                    mapGrid[row][col] = selectedTile;
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the grid and tiles
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;

                // Draw tile color based on type
                switch (mapGrid[row][col]) {
                    case EMPTY -> g.setColor(Color.WHITE);
                    case TERRAIN -> g.setColor(new Color(153, 217, 234)); // light blue
                    case OBSTACLE -> g.setColor(Color.DARK_GRAY);
                    case VEGETATION -> g.setColor(new Color(34, 139, 34)); // forest green
                }

                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                g.setColor(Color.GRAY);
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE); // grid line
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE);
    }

    // For demonstration
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Map Editor");
            MapEditor editor = new MapEditor();

            // Tile selector buttons
            JPanel controls = new JPanel();
            for (TileType type : TileType.values()) {
                JButton btn = new JButton(type.name());
                btn.addActionListener(e -> editor.selectedTile = type);
                controls.add(btn);
            }

            frame.setLayout(new BorderLayout());
            frame.add(editor, BorderLayout.CENTER);
            frame.add(controls, BorderLayout.SOUTH);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
