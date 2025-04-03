

public class TankUnit extends Unit implements gunnable{
    public double protection;
    public double killProbability;
    
    public TankUnit (int x, int y, String type, double protection){
        super(x, y, type);
        this.protection=protection;
        
    }
    @Override
    public void display(){
        System.out.println("This is a tank unit");
        
    }
    
    @Override
    public void fire(Unit target){
        double distance = Math.sqrt(Math.pow(this.x - target.x, 2) + Math.pow(this.y - target.y, 2));

        // Kill probability decreases with distance (min 0.3)
        this.killProbability = Math.max(0.3, 1.0 - (distance / 100.0));
        System.out.println("Target destroyed with probability:"+this.killProbability);

        
    }
     
    
    

}
