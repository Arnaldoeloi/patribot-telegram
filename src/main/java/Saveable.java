import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

abstract class Saveable implements Serializable{

    abstract void save() throws IOException;
    abstract void delete();
    abstract void update();



}
