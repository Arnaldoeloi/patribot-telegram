import java.io.*;

abstract class Saveable implements Serializable{

    protected String codigo;

    protected static final String extension = ".obj";
    protected static final String directory = "objects/";

    protected void save() throws IOException{
        try{
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("storage/" + this.directory + this.codigo + extension));
            os.writeObject(this);
            os.close();
            System.out.println("Bem salvo com sucesso.");
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Bem não pôde ser salvo.");
        }
    }
    protected void delete(){

    }
    protected void update(){

    }



}
