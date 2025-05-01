import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList; // ❗ You forgot to import this
import java.util.List;

public class ScenarioCSVExporter {

  public static void exportUnitsToCSV(BattleManager bm, String filename) throws IOException {
    List<Unit> units = bm.getAllUnits();
    try (FileWriter writer = new FileWriter(filename)) {
        // Writing header with the "z" column
        writer.write("team,type,x,y,z\n");
        
        // Writing each unit's details with "z"
        for (Unit u : units) {
            writer.write(u.team + "," + u.type + "," + u.x + "," + u.y + "," + u.z + "\n");
        }
    }
}



    public static List<Unit> loadUnitsFromCSV(String filename) throws IOException {
        List<Unit> allunits = new ArrayList<>(); // ❗ Was missing import
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) { // skip header
                    firstLine = false;
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 5) continue; // ❗ Changed from 4 to 5 (because you now have z too)

                String team = parts[0].trim();
                String type = parts[1].trim();
                int x = Integer.parseInt(parts[2].trim());
                int y = Integer.parseInt(parts[3].trim());
                int z = Integer.parseInt(parts[4].trim());

                Unit u = UnitFactory.createUnit(team, type, x, y, z); // ❗ Fixed syntax
                allunits.add(u);
            }
        }

        return allunits;
    }
}
