import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Despesa {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private int id;
    private String descricao;
    private double valor;
    private String categoria;
    private LocalDate data;

    public Despesa(int id, String descricao, double valor, String categoria, LocalDate data) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.categoria = categoria;
        this.data = data;
    }

    public int getId() { return id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String paraCSV() {
        return id + ";" + descricao + ";" + valor + ";" + categoria + ";" + data.format(FMT);
    }

    public static Despesa deCSV(String linha) {
        String[] p = linha.split(";");
        return new Despesa(
                Integer.parseInt(p[0]),
                p[1],
                Double.parseDouble(p[2]),
                p[3],
                LocalDate.parse(p[4], FMT)
        );
    }

    @Override
    public String toString() {
        return String.format("#%-3d | %-25s | R$ %10.2f | %-15s | %s",
                id, descricao, valor, categoria, data.format(FMT));
    }
}
