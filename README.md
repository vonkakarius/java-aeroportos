# Trabalho de Java

A solução foi estruturada em 5 arquivos:
- [Aeroporto.java](#aeroportojava) - representa um aeroporto internacional
- [Grafo.java](#grafojava) - representa a rede de voos e implementa o algoritmo de Dijkstra
- [Par.java](#parjava) - representa um par ordenável de dados usado na Priority Queue pelo algoritmo de Dijkstra
- [Main.java](#mainjava) - arquivo principal
- [ConexaoSQL.java](#conexaosqljava) - estabelece conexões com o banco de dados MySQL

## Aeroporto.java
O arquivo completo pode ser acessado [aqui](https://github.com/vonkakarius/java-aeroportos/blob/main/src/Aeroporto.java).

Essa classe representa aeroportos, onde o atributo *codigo* consiste na sigla de 3 letras que identifica cada aeroporto. Implementa-se um construtor e um método para calcular a distância de um aeroporto a outro, o que usaremos no algoritmo de Dijkstra para calcular o peso de cada aresta do grafo.
```java
public class Aeroporto
{
    // Atributos
    public String codigo;
    public String nome;
    public String estado;
    public double latitude;
    public double longitude;

    // Construtor
    public Aeroporto(String codigo, String nome, String estado, double latitude, double longitude)
    {
        this.codigo = codigo;
        this.nome = nome;
        this.estado = estado;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Distância
    public double distanciaPara(Aeroporto vizinho)
    {
        double lat1 = this.latitude, lon1 = this.longitude, lat2 = vizinho.latitude, lon2 = vizinho.longitude;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(lon1-lon2));
        return Math.toDegrees(Math.acos(dist)) * 60 * 1.1515 * 1.609344;
    }
}
```

## Grafo.java
O arquivo completo pode ser acessado [aqui](https://github.com/vonkakarius/java-aeroportos/blob/main/src/Grafo.java).

Essa classe representa um grafo onde cada vértice faz referência a um aeroporto e cada aresta é um voo de um aeroporto a outro. O grafo é ponderado, e o peso de cada aresta é a distância entre os dois aeroportos.

Representamos o grafo por uma matriz de adjacência, guardamos a quantidade de vértices (largura da matriz) e mapeamos cada aeroporto em um índice por meio de sua sigla.
```java
// Atributos
int n;
double[][] matriz;
HashMap<String, Integer> indice;
HashMap<Integer, String> codigo;
```

### Construtor
O construtor recebe uma lista de aeroportos, calcula o peso de cada aresta e atualiza a matriz de adjacência e mapear cada aeroporto no índice correspondente da matriz.
```java
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
```

### Algoritmo de Dijkstra
O único outro método da classe consiste na implementação do algoritmo de Dijkstra utilizando a matriz de adjacência criada.
```java
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
```

## Par.java

O arquivo completo pode ser acessado [aqui](https://github.com/vonkakarius/java-aeroportos/blob/main/src/Par.java). 

Essa classe consiste na representação de um *Par<Integer, Double>* que utilizamos para inserir adequadamente na Priority Queue do Algoritmo de Dijkstra a informação do menor peso encontrado para cada vértice até então.

Para a ordenação automática da Priority Queue, precisamos especificar como comparar um par a outro.

```java
public class Par implements Comparable<Par>
{
    // Atributos
    int i;
    double peso;

    // Construtor
    public Par(int i, double peso)
    {
        this.i = i;
        this.peso = peso;
    }

    // Comparador
    @Override
    public int compareTo(Par outro)
    {
        return (this.peso > outro.peso) ? 1 : -1;
    }
}
```

## Main.java

O arquivo completo pode ser acessado [aqui](https://github.com/vonkakarius/java-aeroportos/blob/main/src/Main.java). 

Além das importações necessárias, a classe possui apenas o método *main*. Nele, o procedimento adotado segue as seguintes etapas.

### Carregamento de Dados
Os aeroportos contidos no banco de dados MySQL são carregados para um *ArrayList*, e um grafo é construído para representar a rede de voos. Além disso, filtramos os estados em que há aeroportos internacionais, para exibir no menu. Criamos um *scanner* e uma variável de controle de loop, para executar quantas consultas o usuário desejar.

```java
// Carrega Dados
ArrayList<Aeroporto> aeroportos = ConexaoSQL.extrairAeroportos();
Grafo grafo = new Grafo(aeroportos);
HashSet<String> estados = new HashSet<>();
for (Aeroporto aero : aeroportos) estados.add(aero.estado);
Scanner sc = new Scanner(System.in);
String continua = "S";
```

### Entrada de Consulta
O trecho seguinte de código é intuitivo e trata apenas da exibição do menu de escolha de aeroportos de origem e destino, com tratamento de padrão de entrada para as siglas de estado e de aeroportos.

```java
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
```

### Cálculo da Solução
Em seguida, verificamos se a consulta que o usuário está fazendo já consta na tabela de consultas do banco de dados, pois nesse caso basta apenas recuperar sua resposta já calculada. Do contrário, implementamos o método de Dijkstra para calcular o voo de conexão que gera a menor rota para o trajeto.

Como a rede foi montada pressupondo-se que é possível ir de qualquer aeroporto para qualquer outro, a rota de Dijkstra, que possui a restrição do trabalho de ter pelo menos uma conexão, terá *exatamente uma*, por desigualdade triangular.

```java
String nomeOrigem = "", nomeConexao = "", nomeDestino = "";

// Pesquisa em Consultas Anteriores
String codConexao = ConexaoSQL.pesquisarConsulta(codOrigem, codDestino);
if (codConexao.isEmpty())
{
    codConexao = grafo.dijkstra(codOrigem, codDestino);
    ConexaoSQL.salvarConsulta(codOrigem, codConexao, codDestino);
}
```

### Exibição do Resultado
Com a solução em mãos, recuperamos o nome dos aeroportos envolvidos e mostramos ao usuário, perguntando se deseja fazer outra consulta.

```java
// Resultado
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
```

Blocos try/catch também foram implementados para tratamento de erro, bem como foi implementado o loop de execução para que o usuário faça quantas consultas desejar.

## ConexaoSQL.java

O arquivo completo pode ser acessado [aqui](https://github.com/vonkakarius/java-aeroportos/blob/main/src/ConexaoSQL.java). 

Além das importações necessárias, a classe possui 3 métodos e os seguintes atributos, que consistem nos dados de acesso ao MySQL:
```java
// Atributos
static String url = "jdbc:mysql://localhost:3306/jbase";
static String user = "appjava";
static String password = "appjava";
```

### extrairAeroportos
Aqui cuidamos de recuperar do BD as informações de todos os aeroportos internacionais brasileiros e criar para cada um deles um objeto da classe *Aeroporto*. Inserimos todos esses objetos em um *ArrayList* e retornamos.
```java
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
```
### pesquisarConsulta
Aqui cuidamos de verificar se uma consulta que está sendo feita já foi resolvida e consta no banco de dados. Se for o caso, retornamos a solução conhecida na forma do código do aeroporto de conexão que minimiza a rota. Senão, retornamos uma string vazia.
```java
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
```
### salvarConsulta
Esse método salva uma consulta no banco de dados para evitar recálculo futuro de uma solução já obtida.
```java
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
```

## Considerações
Os arquivos completos constam na pasta [src](https://github.com/vonkakarius/java-aeroportos/tree/main/src) deste repositório. Foi utilizada também uma biblioteca externa para conexão com o MySQL, que pode ser baixada [nesta página](https://dev.mysql.com/downloads/connector/j/8.0.html).
