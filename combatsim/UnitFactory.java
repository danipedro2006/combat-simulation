public class UnitFactory {

    public static Unit createUnit(String team, String type, int x, int y, int z) {
        switch (type) {
            case "TKS":
                return new TankUnit(x, y, z, 0.6, 8, team, type, 3); // example values
            case "INF":
                return new InfantryUnit(x, y, z, 0.4, 4, team, type);
            case "ARTY":
                return new ArtilleryUnit(x, y, z, 0.8, 60, team, type, 5);
            default:
                throw new IllegalArgumentException("Unknown unit type: " + type);
        }
    }
}
