 

public class UnitFactory {

    public static Unit createUnit(String team, String type, int x, int y, int z) {
        switch (type) {
            case "TKS":
                return new TankUnit(x, y, z, Constants.killTkProbability, 8, team, type, 3); // example values
            case "INF":
                return new InfantryUnit(x, y, z, Constants.killInfProbability, 4, team, type);
            case "ARTY":
                return new ArtilleryUnit(x, y, z, Constants.killArtyProbability, 60, team, type, Constants.voleys);
            case "AD":
                return new AirDefenseUnit(x, y, z, Constants.killADProbability, 60, team, type);

            case "CAS":
                return new CASUnit(x, y, z, Constants.killCASProbability, 4, team, type);

            default:
                throw new IllegalArgumentException("Unknown unit type: " + type);
        }
    }
}
