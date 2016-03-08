package pojos;

/**
 * Created by Arch on 3/7/2016.
 */
public class District {
    public String id, name, geom;

    public String toString(){
        return "Distrito " + name + " con ID " + id +
                "\nGeometría: " + geom;
    }
}
