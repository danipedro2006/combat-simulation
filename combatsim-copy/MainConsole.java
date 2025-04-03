import java.util.ArrayList;

public class MainConsole {
    public static void main(String[] args) {
        ArrayList<Unit> redUnits = new ArrayList<>();
        ArrayList<Unit> blueUnits = new ArrayList<>();

        // Add Red Units
        redUnits.add(new Unit(2, 3, 0.7, 4, "Red"));
        redUnits.add(new Unit(7, 2, 0.6, 3, "Red"));
        
        // Add Blue Units
        blueUnits.add(new Unit(3, 3, 0.8, 5, "Blue"));
        blueUnits.add(new Unit(6, 2, 0.5, 2, "Blue"));

        // Create BattleManager and run combat
        BattleManager battleManager = new BattleManager(redUnits, blueUnits);

        System.out.println("Before Battle:");
        printUnits(redUnits, blueUnits);

        battleManager.findTargets();
        battleManager.simulateCombat();

        System.out.println("After Battle:");
        printUnits(redUnits, blueUnits);
    }

    private static void printUnits(ArrayList<Unit> redUnits, ArrayList<Unit> blueUnits) {
        System.out.println("Red Units:");
        for (Unit u : redUnits) {
            System.out.println(u);
        }
        System.out.println("Blue Units:");
        for (Unit u : blueUnits) {
            System.out.println(u);
        }
        System.out.println("----------------------------");
    }
}
