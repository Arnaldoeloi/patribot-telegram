import java.io.*;

abstract class Saveable implements Serializable{

    protected String codigo;

    protected void save() throws IOException{
        try{
            System.out.println("storage/" + getClassDirectory() + this.codigo + getClassExtension());
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("storage/" + getClassDirectory() + this.codigo + getClassExtension()));
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

    abstract String getClassDirectory();
    abstract String getClassExtension();


}
