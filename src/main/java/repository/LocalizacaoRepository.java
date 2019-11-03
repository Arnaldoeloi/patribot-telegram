package repository;

import model.Localizacao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LocalizacaoRepository {
    private final Conexao conexaoSQL;

    public LocalizacaoRepository(Conexao conn){
        conexaoSQL = conn;
    }

    public  void criarTabela(){
        String sql = "CREATE TABLE IF NOT EXISTS localizacao(\n"
                + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    nome text,\n"
                + "    descricao text"
                + ");";
        conexaoSQL.connect();
        System.out.println(sql);
        Statement stm = conexaoSQL.criarStatement();
        try{
            stm.execute(sql);
            stm.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            conexaoSQL.desconect();
        }
    }
    public void inserir(Localizacao local ){
        String sql = "INSERT INTO localizacao(nome,descricao) VALUES(?,?)";
        conexaoSQL.connect();
        try{
            PreparedStatement stm = conexaoSQL.getConn().prepareStatement(sql);
            stm.setString(1,local.getNome());
            stm.setString(2,local.getDescricao());
            stm.executeUpdate();
            stm.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            conexaoSQL.desconect();
        }

    }

    public  List<Localizacao>  findall(){
        String sql = "SELECT * FROM localizacao";

        try {
            conexaoSQL.connect();
             Statement stmt  = conexaoSQL.getConn().createStatement();
             ResultSet rs    = stmt.executeQuery(sql);
            List<Localizacao> locais = new ArrayList<Localizacao>();
            while (rs.next()) {
                locais.add(new Localizacao(rs.getInt("id"),rs.getString("nome"),rs.getString("descricao")));
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("nome") + "\t" +
                        rs.getString("descricao"));
            }
            stmt.close();
            return locais;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally {
            conexaoSQL.desconect();
        }
        return null;
    }

    public Localizacao findById(Integer id){
        String sql = "SELECT * "
                + "FROM localizacao WHERE id = ?";
        try {
            conexaoSQL.connect();
            PreparedStatement pstmt  = conexaoSQL.getConn().prepareStatement(sql);
            pstmt.setInt(1,id);
            ResultSet rs  = pstmt.executeQuery();
            Localizacao local = new Localizacao(rs.getInt("id"),rs.getString("nome"),rs.getString("descricao"));
            while (rs.next()) {
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("nome") + "\t" +
                        rs.getString("descricao"));
            }
            pstmt.close();
            return local;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }finally {
            conexaoSQL.desconect();
        }
    }
}
