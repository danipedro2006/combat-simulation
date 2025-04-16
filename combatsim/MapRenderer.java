import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.util.Map;

public class MapRenderer {
        
     public static void drawUnits(Graphics g, ArrayList<Unit> units, Color color,  int cellsize) {
        g.setColor(color);
        
        for (Unit unit : units) {
            int x = unit.x * cellsize + cellsize / 2;
            int y = unit.y * cellsize + cellsize / 2;
            int altitude =(int)unit.z;
            if (unit.visible || unit.team.equals("Blue")){
                unit.drawUnit(x,y,g, color);
            }
            
          
            // g.drawString(unit.type.charAt(0) + String.valueOf(index), x - 10, y - 10);
        }
    }

    
    public static void drawAtack(Graphics g,Map<Unit, Unit> attackPairs,Color color, int cellsize){
        g.setColor(color);
        for (Map.Entry<Unit, Unit> entry : attackPairs.entrySet()) {
            Unit attacker = entry.getKey();
            Unit target = entry.getValue();

            // Convert unit positions to pixels (scaling up for visibility)
            int attackerX = attacker.x * cellsize + cellsize / 2;
            int attackerY = attacker.y * cellsize + cellsize / 2;
            int targetX = target.x * cellsize + cellsize / 2;
            int targetY = target.y * cellsize + cellsize / 2;
            g.drawLine(attackerX+Unit.UNIT_L/2, attackerY+Unit.UNIT_W/2, targetX+Unit.UNIT_L/2, targetY+Unit.UNIT_W/2);
        }
        
        
    }
    
    public static void drawImpactPoints(Graphics g, ArrayList<Unit.Point> impactpoints,int cellsize){
        g.setColor(Color.BLACK); // or any effect you want
        for (Unit.Point p : impactpoints) {
            int px = p.x * cellsize + cellsize / 2;
            int py = p.y * cellsize + cellsize / 2;
            //g.fillOval(px - 5, py - 5, 10, 10); // draw small circle at center of cell
            g.drawOval(px - 5, py - 5, ArtilleryUnit.fireRadius, ArtilleryUnit.fireRadius); // draw small circle at center of cell
        }

    }
    
        public static void drawPath(Graphics g,ArrayList<Unit> blueUnits, int cellsize ){
        g.setColor(Color.BLACK); // Or whatever color for paths

        for (Unit unit : blueUnits) { // redUnits + blueUnits
            System.out.println("First unit class: " + blueUnits.get(0).getClass().getName());
            List<Unit.Point> path = unit.getPath();
            for (int i = 0; i < path.size() - 1; i++) {
                Unit.Point p1 = path.get(i);
                Unit.Point p2 = path.get(i + 1);
                int x1 = p1.x * cellsize + cellsize / 2;
                int y1 = p1.y * cellsize + cellsize / 2;
                int x2 = p2.x * cellsize + cellsize / 2;
                int y2 = p2.y * cellsize + cellsize / 2;
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }

}
