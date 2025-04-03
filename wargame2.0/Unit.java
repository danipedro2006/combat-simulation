
public class Unit {
    public int x, y;
    public String type;
    
    public Unit(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
        
        
    }
    
   
    
    public static void main(String[] args){
        System.out.println("working");
        
        Unit unit=new Unit(10, 10 , "generic");
        Unit target=new Unit(40, 40, "target");
         
        unit.display();
        
        TankUnit tankUnit=new TankUnit(20, 30, "tank unit", 0.9);
        tankUnit.display();
        tankUnit.fire(target);
    }
      
    public void display(){
        System.out.println("This is generic unit");
        
    }
       
    }

   
    
   

