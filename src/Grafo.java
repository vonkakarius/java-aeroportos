import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Grafo
{
    // Atributos
    int n;
    double[][] matriz;
    HashMap<String, Integer> indice;
    HashMap<Integer, String> codigo;

    // Construtor
    public Grafo(ArrayList<Aeroporto> lista)
    {
        n = lista.size();
        matriz = new double[n][n];
        indice = new HashMap<>();
        codigo = new HashMap<>();

        for (int i = 0; i < n; i++)
        {
            indice.put(lista.get(i).codigo, i);
            codigo.put(i, lista.get(i).codigo);

            for (int j = i; j < n; j++)
                matriz[i][j] = matriz[j][i] = lista.get(i).distanciaPara(lista.get(j));
        }
    }

    // Dijkstra
    public String dijkstra(String codOrigem, String codDestino)
    {
        int origem = indice.get(codOrigem);
        int destino = indice.get(codDestino);
        String codConexao = "";
        double[] menorCusto = new double[n];
        boolean[] visitados = new boolean[n];
        PriorityQueue<Par> pq = new PriorityQueue<>();

        for (int i = 0; i < n; i++) menorCusto[i] = 999999999;
        menorCusto[origem] = 0;
        pq.add(new Par(origem, 0));

        while (!pq.isEmpty())
        {
            int atual = pq.poll().i;
            visitados[atual] = true;

            for (int vizinho = 0; vizinho < n; vizinho++)
            {
                // Vôo Direto Proibido
                if (atual == origem && vizinho == destino) continue;

                double passo = matriz[atual][vizinho];
                if (!visitados[vizinho] && passo > 0)
                {
                    double custoAntigo = menorCusto[vizinho];
                    double custoNovo = menorCusto[atual] + passo;
                    
                    if (custoNovo < custoAntigo)
                    {
                        menorCusto[vizinho] = custoNovo;
                        pq.add(new Par(vizinho, custoNovo));
                        if (vizinho == destino) codConexao = codigo.get(atual);
                    }
                }
            }
        }

        return codConexao;
    }
}