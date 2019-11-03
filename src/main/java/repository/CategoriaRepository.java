package repository;

import java.sql.SQLException;
import java.sql.Statement;

public class CategoriaRepository {

    private final Conexao conexaoSQL;

    public CategoriaRepository(Conexao conn){
        conexaoSQL = conn;
    }

    public  void criarTabela(){
        String sql = "CREATE TABLE IF NOT EXISTS categoria(\n"
                + "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "    codigo text NOT NULL,\n"
                + "    nome text,\n"
                + "    descricao text"
                + ");";
        conexaoSQL.connect();
        System.out.println(sql);
        Statement stm = conexaoSQL.criarStatement();
        try{
            stm.execute(sql);
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }
}
