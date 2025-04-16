import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.HashSet;
import java.util.List;

class BattleManager {
    public ArrayList<Unit> redUnits;
    public ArrayList<Unit> blueUnits;
    public ArrayList<Unit> allUnits;
    private Map<Unit, Unit> attackPairs = new HashMap<>();
    private Random random = new Random();
    ArrayList<Unit> toRemove = new ArrayList<>();
    private int initialRedCount;
    private int initialBlueCount;

    public BattleManager(ArrayList<Unit> redUnits, ArrayList<Unit> blueUnits) {
        this.redUnits = redUnits;
        this.blueUnits = blueUnits;
        for (Unit red : redUnits) {
            red.sensorRange = 6;
        }

        for (Unit blue : blueUnits) {
            blue.sensorRange = 3;
        }

        this.initialRedCount = redUnits.size();
        this.initialBlueCount = blueUnits.size();
    }

    public Map<Unit, Unit> getAttackPairs() {
        return attackPairs;
    }

    // Find targets in range and store them
    public void findTargets() {

        attackPairs.clear();

        for (Unit red : redUnits) {

            for (Unit blue : blueUnits) {
                if (!blue.visible) continue;
                if (red.canAttack(blue)) {
                    attackPairs.put(red, blue);
                    break; // One attack per unit
                }
            }
        }

        for (Unit blue : blueUnits) {
            for (Unit red : redUnits) {
                if (!red.visible) continue;
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
                System.out.println(attacker.type + " at (" + attacker.x + ", " + attacker.y + ") killed " +
                    target.type + " at (" + target.x + ", " + target.y + ")");
            }

            processed.add(attacker); // Mark attacker as processed
        }

        // Remove killed units
        redUnits.removeAll(toRemove);
        blueUnits.removeAll(toRemove);

    }

    public void clearDestroyedTargets(){
        Unit arty = findArtyUnit();
        if (arty instanceof ArtilleryUnit) {
            toRemove.addAll(((ArtilleryUnit) arty).getDestroyedTargets());
            redUnits.removeAll(toRemove);

        }
    }

    public String getInitialForceRatio() {
        if (initialBlueCount == 0) return "Infinite (no blue units)";
        double ratio = (double) initialRedCount / initialBlueCount;
        return String.format("Initial Force Ratio: Red : Blue = %.2f : 1", ratio);
    }
    
    

   


    public String getLossesReport() {
        int currentRed = redUnits.size();
        int currentBlue = blueUnits.size();

        int redLosses = initialRedCount - currentRed;
        int blueLosses = initialBlueCount - currentBlue;

        double redLossPct = (redLosses * 100.0) / initialRedCount;
        double blueLossPct = (blueLosses * 100.0) / initialBlueCount;

        return String.format(
            "Red losses: %d (%.1f%%) | Blue losses: %d (%.1f%%)",
            redLosses, redLossPct, blueLosses, blueLossPct
        );
    }
    
    public List<Unit> getAllUnits(){
        List<Unit> allUnits = new ArrayList<>();
        if (redUnits != null) {
            allUnits.addAll(redUnits);
        }
        if (blueUnits != null) {
            allUnits.addAll(blueUnits);
        }
        return allUnits;
    
    }

}
