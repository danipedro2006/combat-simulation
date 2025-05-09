
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Map;
import java.util.List;

public class CombatSimulator {

    private Map<Unit, Unit> attackPairs = new HashMap<>();
    private Random random = new Random();
    ArrayList<Unit> toRemove = new ArrayList<>();
    private int initialRedCount;
    private int initialBlueCount;

    public  int GRID_SIZE = Terrain.GRID_SIZE;
    public  int MAP_SIZE = Terrain.MAP_SIZE;
    public  int CELL_SIZE =Terrain.CELL_SIZE;
    private  final int MAX_ALTITUDE =Terrain.MAX_ALTITUDE;
    private  double[][] altitudeGrid =Terrain.altitudeGrid;

    public String[] unitTypes = { "INF", "TKS", "ARTY", "AD", "CAS" };

    public static ArrayList<Unit> redUnits=new ArrayList<>();
    public static ArrayList<Unit> blueUnits=new ArrayList<>();

    public Map<Unit, Unit> attackpairs;
    public String selectedTeam = "Red"; // Default selection
    public ArtilleryUnit selectedArtillery = null;
    public ArrayList<Unit.Point> impactPoints = new ArrayList<>();
    public Unit pathTargetUnit = null; // the unit for which you are setting a path

    public int artileryTgX;
    public int artileryTgY;

    public  boolean isShowAllUnits = false;
    public boolean isPathDrawing = false;
    public boolean isTargeting=false;

    public  boolean[][] obstructingCells =Terrain.obstructingCells;

    public CombatSimulator() {

        for (Unit red : redUnits) {
            red.sensorRange=Constants.redSensorRange;
        }

        for (Unit blue : blueUnits) {
            blue.sensorRange = Constants.blueSensorRange;
        }

        // Update initial counts
        this.initialRedCount = redUnits.size();
        this.initialBlueCount = blueUnits.size();

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

    public Unit getUnitAt(int x, int y) {
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

    public Map<Unit, Unit> getAttackPairs(CombatSimulator simulator) {
        return attackPairs;
    }

    // Find targets in range and store them
    public void findTargets() {

        attackPairs.clear();

        for (Unit red : redUnits) {

            for (Unit blue : blueUnits) {
                if (!blue.visible)
                    continue;
                if (red.canAttack(blue)) {
                    attackPairs.put(red, blue);
                    break; // One attack per unit
                }
            }
        }

        for (Unit blue : blueUnits) {
            for (Unit red : redUnits) {
                if (!red.visible)
                    continue;
                if (blue.canAttack(red)) {
                    attackPairs.put(blue, red);
                    break;
                }
            }
        }
    }

    public void updateVisibility() {
        for (Unit blue : blueUnits) {
            blue.visible = false; // reset
            for (Unit red : redUnits) {
                if (blue.isInSensorRangeOf(red)) {
                    blue.visible = true;
                    break;
                }
            }
        }

        for (Unit red : redUnits) {
            red.visible = false;
            for (Unit blue : blueUnits) {
                if (red.isInSensorRangeOf(blue)) {
                    red.visible = true;
                    break;
                }
            }
        }
    }

    public Unit findArtyUnit() {
        for (Unit blue : blueUnits) {
            if (blue.type.equals("ARTY")) {
                System.out.println("Found arty");
                return (ArtilleryUnit) blue;
            }

        }
        System.out.println("NO arty");
        return null; // <-- Add this to handle the case where no ARTY is found
    }

    public void moveUnits() {
        for (Unit blue : blueUnits) {
            blue.move();
        }
    }

    // Simulate battle
    public void simulateCombat() {

        HashSet<Unit> processed = new HashSet<>(); // Track attackers who already attacked

        for (Map.Entry<Unit, Unit> pair : attackPairs.entrySet()) {
            Unit attacker = pair.getKey();
            Unit target = pair.getValue();

            // Skip if the target has already attacked someone else
            if (processed.contains(target)) {
                continue;
            }

            double roll = random.nextDouble(); // Random 0.0 - 1.0

            if (roll < attacker.killProbability) {
                toRemove.add(target); // Mark target for removal
                System.out.println(attacker.type + " at (" + attacker.x + ", " + attacker.y + ") killed " + target.type
                    + " at (" + target.x + ", " + target.y + ")");
            }

            processed.add(attacker); // Mark attacker as processed
        }

        // Remove killed units
        redUnits.removeAll(toRemove);
        blueUnits.removeAll(toRemove);

    }

    public void clearDestroyedTargets() {
        Unit arty = findArtyUnit();
        if (arty instanceof ArtilleryUnit) {
            toRemove.addAll(((ArtilleryUnit) arty).getDestroyedTargets());
            redUnits.removeAll(toRemove);

        }
    }

    public String getInitialForceRatio() {
        if (this.initialBlueCount == 0)
            return "Infinite (no blue units)";
        double ratio = (double) this.initialRedCount / this.initialBlueCount;
        return String.format("Initial Force Ratio: Red : Blue = %.2f : 1", ratio);
    }

    public String getLossesReport() {
        int currentRed = redUnits.size();
        int currentBlue = blueUnits.size();

        int redLosses = this.initialRedCount - currentRed;
        int blueLosses = this.initialBlueCount - currentBlue;

        double redLossPct = (redLosses * 100.0) / this.initialRedCount;
        double blueLossPct = (blueLosses * 100.0) / this.initialBlueCount;

        return String.format("Red losses: %d (%.1f%%) | Blue losses: %d (%.1f%%)", redLosses, redLossPct, blueLosses,
            blueLossPct);
    }

    public static List<Unit> getAllUnits() {
        List<Unit> allUnits = new ArrayList<>();
        if (redUnits != null) {
            allUnits.addAll(redUnits);
        }
        if (blueUnits != null) {
            allUnits.addAll(blueUnits);
        }
        return allUnits;

    }

    public void loadUnitsFromCSV(String filename) throws IOException {
        System.out.println("Load scenario called");
        List<Unit> allUnits = ScenarioCSVExporter.loadUnitsFromCSV(filename);

        redUnits.clear();
        blueUnits.clear();

        for (Unit u : allUnits) {
            if ("red".equalsIgnoreCase(u.team)) {
                redUnits.add(u);
            } else if ("blue".equalsIgnoreCase(u.team)) {
                blueUnits.add(u);
            } else {
                System.out.println("Unknown team: " + u.team + " — unit skipped.");
            }
        }

        // Set sensor ranges
        for (Unit red : redUnits) {
            red.sensorRange = 6;
        }
        for (Unit blue : blueUnits) {
            blue.sensorRange = 3;
        }

        // Update initial counts
        this.initialRedCount = redUnits.size();
        this.initialBlueCount = blueUnits.size();
    }
}



