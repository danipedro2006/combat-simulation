import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

class BattleManager {
    private ArrayList<Unit> redUnits;
    private ArrayList<Unit> blueUnits;
    private Map<Unit, Unit> attackPairs = new HashMap<>();
    private Random random = new Random();

    public BattleManager(ArrayList<Unit> redUnits, ArrayList<Unit> blueUnits) {
        this.redUnits = redUnits;
        this.blueUnits = blueUnits;
    }

    // Find targets in range and store them
    public void findTargets() {
        attackPairs.clear();
        for (Unit red : redUnits) {
            for (Unit blue : blueUnits) {
                if (red.canAttack(blue)) {
                    attackPairs.put(red, blue);
                    break; // One attack per unit
                }
            }
        }

        for (Unit blue : blueUnits) {
            for (Unit red : redUnits) {
                if (blue.canAttack(red)) {
                    attackPairs.put(blue, red);
                    break;
                }
            }
        }
    }

    // Simulate battle
    public void simulateCombat() {
        ArrayList<Unit> toRemove = new ArrayList<>();

        for (Map.Entry<Unit, Unit> pair : attackPairs.entrySet()) {
            Unit attacker = pair.getKey();
            Unit target = pair.getValue();

            double roll = random.nextDouble(); // Random 0.0 - 1.0
            if (roll < attacker.killProbability) {
                toRemove.add(target); // Mark for removal
                System.out.println(attacker.type + " at (" + attacker.x + ", " + attacker.y + ") killed " +
                        target.type + " at (" + target.x + ", " + target.y + ")");
            }
        }

        // Remove killed units
        redUnits.removeAll(toRemove);
        blueUnits.removeAll(toRemove);
    }
}
