package repository;

import model.Bem;
import model.Categoria;
import model.Localizacao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BemRepository {

    private final Conexao conexaoSQL;
    private  final CategoriaRepository categoriaRepository;
    private  final LocalizacaoRepository localizacaoRepository;
    public BemRepository(Conexao conexaoSQL) {
        this.conexaoSQL = conexaoSQL;
        categoriaRepository = new CategoriaRepository(conexaoSQL);
        localizacaoRepository = new LocalizacaoRepository(conexaoSQL);
    }

    public  void criarTabela(){
        String sql = "CREATE TABLE IF NOT EXISTS bem(\n"
                + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    nome text,\n"
                + "    descricao text,\n"
                + "    localizacao INTEGER,\n"
                + "    categoria INTEGER"
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
    public void inserir(Bem bem ){
        String sql = "INSERT INTO bem(nome,descricao,localizacao,categoria) VALUES(?,?,?,?)";
        conexaoSQL.connect();
        try{
            PreparedStatement stm = conexaoSQL.getConn().prepareStatement(sql);
            stm.setString(1,bem.getNome());
            stm.setString(2,bem.getDescricao());
            stm.setInt(3,bem.getLocalizacao().getId());
            stm.setInt(4,bem.getCategoria().getId());
            stm.executeUpdate();
            stm.close();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }finally {
            conexaoSQL.desconect();
        }
    }
    public Bem findById(Integer id){
        String sql = "SELECT * "
                + "FROM bem WHERE id = ?";
        try {
            conexaoSQL.connect();
            PreparedStatement pstmt  = conexaoSQL.getConn().prepareStatement(sql);
            pstmt.setInt(1,id);
            ResultSet rs  = pstmt.executeQuery();
            Localizacao local = localizacaoRepository.findById(rs.getInt("localizacao"));
            Categoria categoria = categoriaRepository.findById(rs.getInt("categoria"));
            Bem bem = new Bem(rs.getInt("id"),rs.getString("nome"),rs.getString("descricao"),local,categoria);
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("nome") + "\t" +
                        rs.getString("descricao"));
            pstmt.close();
            return bem;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }finally {
            conexaoSQL.desconect();
        }
    }
    public List<Bem> findall(){
        String sql = "SELECT * FROM bem";
        Localizacao local;
        Categoria categoria ;
        try {
            conexaoSQL.connect();
            Statement stmt  = conexaoSQL.getConn().createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            List<Bem> bens = new ArrayList<Bem>();
            while (rs.next()) {
                local = localizacaoRepository.findById(rs.getInt("localizacao"));
                categoria = categoriaRepository.findById(rs.getInt("categoria"));
                bens.add(new Bem(rs.getInt("id"),rs.getString("nome"),rs.getString("descricao"),local,categoria));
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("nome") + "\t" +
                        rs.getString("descricao"));
            }
            stmt.close();
            return bens;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally {
            conexaoSQL.desconect();
        }
        return null;
    }

    public List<Bem> findByLocal (String localizacao){
        String sql = "SELECT * FROM bem WHERE localizacao = ?";
        Localizacao local = localizacaoRepository.findByName(localizacao);
        Categoria categoria ;
        try {
            conexaoSQL.connect();
            PreparedStatement pstmt  = conexaoSQL.getConn().prepareStatement(sql);
            pstmt.setInt(1,local.getId());
            ResultSet rs  = pstmt.executeQuery();
            List<Bem> bens = new ArrayList<Bem>();
            while (rs.next()) {
                categoria = categoriaRepository.findById(rs.getInt("categoria"));
                bens.add(new Bem(rs.getInt("id"),rs.getString("nome"),rs.getString("descricao"),local,categoria));
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("nome") + "\t" +
                        rs.getString("descricao"));
            }
            pstmt.close();
            return bens;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally {
            conexaoSQL.desconect();
        }
        return null;
    }
    public List<Bem> findByName(String nome){
        String sql = "SELECT * "
                + "FROM bem WHERE nome LIKE ?";
        try {
            conexaoSQL.connect();
            PreparedStatement pstmt  = conexaoSQL.getConn().prepareStatement(sql);
            pstmt.setString(1,"%"+nome+ "%");
            ResultSet rs  = pstmt.executeQuery();
            Localizacao local = null;
            Categoria categoria = null ;
            List<Bem> bens = new ArrayList<Bem>();
            while (rs.next()) {
                local = localizacaoRepository.findById(rs.getInt("localizacao"));
                categoria = categoriaRepository.findById(rs.getInt("categoria"));
                bens.add(new Bem(rs.getInt("id"),rs.getString("nome"),rs.getString("descricao"),local,categoria));
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("nome") + "\t" +
                        rs.getString("descricao"));
            }
            pstmt.close();
            return bens;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }finally {
            conexaoSQL.desconect();
        }
    }
    public List<Bem> findByDescription(String descricao){
        String sql = "SELECT * "
                + "FROM bem WHERE descricao LIKE ?";
        try {
            conexaoSQL.connect();
            PreparedStatement pstmt  = conexaoSQL.getConn().prepareStatement(sql);
            pstmt.setString(1,"%"+descricao+"%");
            ResultSet rs  = pstmt.executeQuery();
            Localizacao local = null;
            Categoria categoria = null ;
            List<Bem> bens = new ArrayList<Bem>();
            while (rs.next()) {
                local = localizacaoRepository.findById(rs.getInt("localizacao"));
                categoria = categoriaRepository.findById(rs.getInt("categoria"));
                bens.add(new Bem(rs.getInt("id"),rs.getString("nome"),rs.getString("descricao"),local,categoria));
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("nome") + "\t" +
                        rs.getString("descricao"));
            }
            pstmt.close();
            return bens;
        }catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }finally {
            conexaoSQL.desconect();
        }
    }
    public void update( Bem bem) {
        String sql = "UPDATE bem SET nome = ? , "
                + "descricao = ? , localizacao = ? , categoria = ? "
                + "WHERE id = ?";

        try {
            conexaoSQL.connect();
            PreparedStatement pstmt = conexaoSQL.getConn().prepareStatement(sql);
            // set the corresponding param
            pstmt.setString(1, bem.getNome());
            pstmt.setString(2,bem.getDescricao());
            pstmt.setInt(3, bem.getLocalizacao().getId());
            pstmt.setInt(4,bem.getCategoria().getId());
            pstmt.setInt(5,bem.getId());
            // update
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally {
            conexaoSQL.desconect();
        }
    }

    public List<Bem> findallOrder(){
        String sql = "SELECT * FROM bem ORDER BY localizacao ASC, categoria ASC, nome ASC;";
        Localizacao local;
        Categoria categoria ;
        try {
            conexaoSQL.connect();
            Statement stmt  = conexaoSQL.getConn().createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            List<Bem> bens = new ArrayList<Bem>();
            while (rs.next()) {
                local = localizacaoRepository.findById(rs.getInt("localizacao"));
                categoria = categoriaRepository.findById(rs.getInt("categoria"));
                bens.add(new Bem(rs.getInt("id"),rs.getString("nome"),rs.getString("descricao"),local,categoria));
                System.out.println(rs.getInt("id") +  "\t" +
                        rs.getString("nome") + "\t" +
                        rs.getString("descricao"));
            }
            stmt.close();
            return bens;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally {
            conexaoSQL.desconect();
        }
        return null;
    }
}
