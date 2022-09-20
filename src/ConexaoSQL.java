import java.sql.*;
import java.util.ArrayList;

public class ConexaoSQL
{
    // Atributos
    static String url = "jdbc:mysql://localhost:3306/jbase";
    static String user = "appjava";
    static String password = "appjava";

    // Extração da Tabela de Aeroportos
    public static ArrayList<Aeroporto> extrairAeroportos()
    {
        try (Connection conexao = DriverManager.getConnection(url, user, password)) {
            ResultSet rs = conexao.createStatement().executeQuery("SELECT * FROM aeroportos");
            ArrayList<Aeroporto> lista = new ArrayList<>();

            while (rs.next())
            {
                String codigo = rs.getString(1);
                String nome = rs.getString(2);
                String estado = rs.getString(3);
                double latitude = rs.getDouble(4);
                double longitude = rs.getDouble(5);
                lista.add(new Aeroporto(codigo, nome, estado, latitude, longitude));
            }

            rs.close();
            return lista;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    // Pesquisa na Tabela de Consultas
    public static String pesquisarConsulta(String codOrigem, String codDestino)
    {
        try (Connection conexao = DriverManager.getConnection(url, user, password)) {
            try (ResultSet rs = conexao.createStatement().executeQuery(String.format("SELECT Conexao FROM consultas WHERE Origem = '%s' AND Destino = '%s'", codOrigem, codDestino))) {
                if (!rs.isBeforeFirst()) return "";
                rs.next();
                return rs.getString(1);
            } catch (SQLException e) {
                throw new IllegalStateException("Erro ao pesquisar a consulta:", e);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao acessar o BD:", e);
        }
    }

    // Salvamento na Tabela de Consultas
    public static void salvarConsulta(String codOrigem, String codConexao, String codDestino)
    {
        try (Connection conexao = DriverManager.getConnection(url, user, password)) {
            try {
                conexao.createStatement().executeUpdate(String.format("INSERT INTO consultas (Origem, Conexao, Destino) VALUES ('%s', '%s', '%s')", codOrigem, codConexao, codDestino));
            } catch (SQLException e) {
                throw new IllegalStateException("Erro ao salvar a consulta:", e);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erro ao acessar o BD:", e);
        }
    }
}