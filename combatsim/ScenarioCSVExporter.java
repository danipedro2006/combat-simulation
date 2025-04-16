
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ScenarioCSVExporter {
    public static void exportUnitsToCSV(BattleManager bm, String filename) throws IOException {
        List<Unit> units = bm.getAllUnits();
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("team,type,x,y\n");
            for (Unit u : units) {
                writer.write(u.team + "," + u.type + "," + u.x + "," + u.y + "\n");
            }
        }
    }
}