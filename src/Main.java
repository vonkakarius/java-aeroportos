import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;

public class Main
{
    public static void main(String[] args)
    {
        // Carrega Dados
        ArrayList<Aeroporto> aeroportos = ConexaoSQL.extrairAeroportos();
        Grafo grafo = new Grafo(aeroportos);
        HashSet<String> estados = new HashSet<>();
        for (Aeroporto aero : aeroportos) estados.add(aero.estado);
        Scanner sc = new Scanner(System.in);
        String continua = "S";

        do
        {
            try
            {
                // Entrada
                System.out.println("--------------------------------------------------------------------");
                System.out.println("|                   DIJKSTRA EM VIAGENS AÉREAS                     |");
                System.out.println("--------------------------------------------------------------------");
                System.out.println("|                       Estado de Origem                           |");
                System.out.println("--------------------------------------------------------------------");
                for (String estado : estados) System.out.printf(" > %s%n", estado);
                System.out.print("\n Informe a sigla do estado de origem: ");
                String estadoOrigem = sc.next("[A-Z]{2}");

                System.out.println("--------------------------------------------------------------------");
                System.out.println("|                      Aeroporto de Origem                         |");
                System.out.println("--------------------------------------------------------------------");
                for (Aeroporto aero : aeroportos)
                    if (aero.estado.contains(String.format("(%s)", estadoOrigem)))
                        System.out.printf(" > %s - %s%n", aero.codigo, aero.nome);
                System.out.print("\n Informe a sigla do aeroporto de origem: ");
                String codOrigem = sc.next("[A-Z]{3}");

                System.out.println("--------------------------------------------------------------------");
                System.out.println("|                       Estado de Destino                          |");
                System.out.println("--------------------------------------------------------------------");
                for (String estado : estados) System.out.printf(" > %s%n", estado);
                System.out.print("\n Informe a sigla do estado de destino: ");
                String estadoDestino = sc.next("[A-Z]{2}");

                System.out.println("--------------------------------------------------------------------");
                System.out.println("|                      Aeroporto de Destino                        |");
                System.out.println("--------------------------------------------------------------------");
                for (Aeroporto aero : aeroportos)
                    if (aero.estado.contains(String.format("(%s)", estadoDestino)))
                        System.out.printf(" > %s - %s%n", aero.codigo, aero.nome);
                System.out.print("\n Informe a sigla do aeroporto de destino: ");
                String codDestino = sc.next("[A-Z]{3}");

                // Resultado
                String nomeOrigem = "", nomeConexao = "", nomeDestino = "";

                // Pesquisa em Consultas Anteriores
                String codConexao = ConexaoSQL.pesquisarConsulta(codOrigem, codDestino);
                if (codConexao.isEmpty())
                {
                    codConexao = grafo.dijkstra(codOrigem, codDestino);
                    ConexaoSQL.salvarConsulta(codOrigem, codConexao, codDestino);
                }

                for (Aeroporto aero : aeroportos)
                {
                    if (aero.codigo.equals(codOrigem)) nomeOrigem = aero.nome;
                    else if (aero.codigo.equals(codConexao)) nomeConexao = aero.nome;
                    else if (aero.codigo.equals(codDestino)) nomeDestino = aero.nome;
                }

                System.out.println("--------------------------------------------------------------------");
                System.out.println("|                     Conexão de Menor Rota                        |");
                System.out.println("--------------------------------------------------------------------");
                System.out.printf(" > Partida: %s - %s%n", codOrigem, nomeOrigem);
                System.out.printf(" > Conexão: %s - %s%n", codConexao, nomeConexao);
                System.out.printf(" > Chegada: %s - %s%n", codDestino, nomeDestino);
                System.out.println("--------------------------------------------------------------------");

                // Pergunta de Loop
                System.out.print("Nova consulta? [S/N]: ");
                continua = sc.next("[SN]");
            }
            catch (InputMismatchException e)
            {
                System.out.println("Por favor, insira as informações corretamente.");
                sc.nextLine();
            }
        } while (continua.equals("S"));
    }
}