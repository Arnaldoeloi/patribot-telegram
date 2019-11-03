import java.io.*;

public class Bem extends Saveable{
    private String codigo, nome, descricao;
    private Localizacao localizacao;
    private Categoria categoria;

    private static final String extension = ".bem";

    public Bem(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }
    public Bem(String nome, String descricao, Localizacao localizacao) {
        this.nome = nome;
        this.descricao = descricao;
        this.localizacao = localizacao;
    }
    public Bem(String nome, String descricao, Localizacao localizacao, Categoria categoria) {
        this.nome = nome;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.categoria = categoria;
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

    void save() throws IOException {
        try{
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(this.codigo+extension));
            os.writeObject(this);
            os.close();
            System.out.println("Bem salvo com sucesso.");
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("Bem não pôde ser salvo.");
        }
    }

    void delete() {

    }

    void update() {

    }

    public static Bem get(String codigo){
        try{
            ObjectInputStream is = new ObjectInputStream(new FileInputStream("storage/"+codigo+extension));
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
