class Unit {
    int x, y;         // Position on the grid
    double killProbability;  // Probability of a successful attack
    double range;     // Attack range in grid units
    String type;      // "Red" or "Blue"

    public Unit(int x, int y, double killProbability, double range, String type) {
        this.x = x;
        this.y = y;
        this.killProbability = killProbability;
        this.range = range;
        this.type = type;
    }

    // Check if this unit can attack another unit based on range
    public boolean canAttack(Unit target) {
        double distance = Math.sqrt(Math.pow(this.x - target.x, 2) + Math.pow(this.y - target.y, 2));
        return distance <= this.range;
    }

    @Override
    public String toString() {
        return type + " Unit at (" + x + ", " + y + ") - Kill Prob: " + killProbability + ", Range: " + range;
    }
}
