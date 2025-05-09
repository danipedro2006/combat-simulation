 
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Map;
import java.util.List;

import javax.swing.SwingUtilities;

public class CSApp {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Terrain terrain = new Terrain();
            terrain.loadTerrainElevation();

            CombatSimulator combatSimulator = new CombatSimulator();
            

            BattleReportWindow reportWindow = new BattleReportWindow("Battle Reports");
            
            UIManager uimanager=new UIManager(combatSimulator, reportWindow);
            uimanager.setupUI();
            
            
            
        });
    }
}
