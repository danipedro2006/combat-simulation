
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.List;

public class Test {
    
    public static void greet(Consumer<String> s, String name){
        s.accept(name);
        
    }
    
    public static void shoot(Consumer<String> s, String s2){
        s.accept(s2);
    }
    
    public static void main(String[] args){
        
        Consumer<String> salute=(s)->System.out.println("Salut "+s);
        Consumer<String> shoot=(s)->System.out.println(s.toUpperCase());
        
        //salute.accept("Daniel");
        //shoot.accept("Daniel");
        //salute.andThen(shoot).accept("Daniel");
        //shoot.andThen(salute).accept("Daniel");
        
        List<String> names=List.of("Daniel","Bill","Jonny");
        
        names.forEach(salute.andThen(shoot));
        
        
        
        
    }

}
