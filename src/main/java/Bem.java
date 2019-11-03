import java.io.*;
import java.util.Date;

public class Bem extends Saveable{
    private String nome, descricao;
    private Localizacao localizacao;
    private Categoria categoria;


    protected static final String extension = ".bem";
    protected static final String directory = "bens/";


    public Bem(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;

        this.generateCodigo();
    }
    public Bem(String nome, String descricao, Localizacao localizacao) {
        this.nome = nome;
        this.descricao = descricao;
        this.localizacao = localizacao;

        this.generateCodigo();
    }
    public Bem(String nome, String descricao, Localizacao localizacao, Categoria categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.categoria = categoria;

        this.generateCodigo();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public void delete() {

    }

    public void update() {

    }

    String getClassDirectory() {
        return directory;
    }

    String getClassExtension() {
        return extension;
    }

    private void generateCodigo(){
        Date d = new Date();
        System.out.println(d.getTime());
        this.codigo = String.valueOf(d.getTime());
    }
    public static Bem get(String codigo){
        try{

            ObjectInputStream is = new ObjectInputStream(new FileInputStream("storage/" + directory + codigo + extension));
            System.out.println("storage/" + directory + codigo + extension);
            Bem bem = (Bem) is.readObject();
            is.close();
            return bem;
        }catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
