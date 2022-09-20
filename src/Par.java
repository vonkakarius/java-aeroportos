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