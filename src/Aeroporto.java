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