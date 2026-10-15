import java.io.IOException;              // representa erros ao mexer em arquivos
import java.nio.file.Files;             // ler e escrever arquivos de forma simples
import java.nio.file.Path;              // representa o caminho de um arquivo
import java.util.ArrayList;             // uma lista que cresce conforme adicionamos itens
import java.util.LinkedHashMap;         // um mapa (chave -> valor) que mantém a ordem de inserção
import java.util.List;                  // o "tipo" lista, de forma genérica
import java.util.Map;                   // o "tipo" mapa, de forma genérica

public class GerenciadorDespesas {

    private final List<Despesa> despesas = new ArrayList<>();

    private final Path arquivo = Path.of("despesas.csv");

    private int proximoId = 1;

    public GerenciadorDespesas() {
        carregar();
    }

    public Despesa adicionar(String descricao, double valor, String categoria, java.time.LocalDate data) {
        // Usamos proximoId e depois somamos 1 (proximoId++) para o próximo ficar diferente.
        Despesa d = new Despesa(proximoId++, descricao, valor, categoria, data);
        despesas.add(d);   // coloca na lista
        salvar();          // grava no arquivo para não perder
        return d;          // devolve a despesa criada (útil para mostrar na tela)
    }

    public List<Despesa> listar() {
        // Devolvemos uma nova lista (cópia) para proteger a lista original.
        return new ArrayList<>(despesas);
    }

    public Despesa buscarPorId(int id) {
        // "for-each": percorre cada despesa 'd' dentro da lista.
        for (Despesa d : despesas) {
            if (d.getId() == id) {
                return d;
            }
        }
        return null;
    }

    public boolean atualizar(int id, String descricao, double valor, String categoria, java.time.LocalDate data) {
        Despesa d = buscarPorId(id);
        if (d == null) {
            return false;
        }
        d.setDescricao(descricao);
        d.setValor(valor);
        d.setCategoria(categoria);
        d.setData(data);
        salvar();
        return true;
    }

    public boolean remover(int id) {
        Despesa d = buscarPorId(id);
        if (d == null) {
            return false;
        }
        despesas.remove(d);
        salvar();
        return true;
    }

    public double totalGeral() {
        double total = 0;
        for (Despesa d : despesas) {
            total += d.getValor();   // total = total + valor da despesa
        }
        return total;
    }

    public Map<String, Double> totalPorCategoria() {
        Map<String, Double> mapa = new LinkedHashMap<>();
        for (Despesa d : despesas) {
            mapa.merge(d.getCategoria(), d.getValor(), Double::sum);
        }
        return mapa;
    }

    public boolean estaVazio() {
        return despesas.isEmpty();
    }

    private void salvar() {
        List<String> linhas = new ArrayList<>();
        for (Despesa d : despesas) {
            linhas.add(d.paraCSV());
        }
        try {
            Files.write(arquivo, linhas);
        } catch (IOException e) {
            System.out.println("⚠️  Não consegui salvar no arquivo: " + e.getMessage());
        }
    }

    private void carregar() {
        // Se o arquivo ainda não existe (primeira vez que roda), não há nada para carregar.
        if (!Files.exists(arquivo)) {
            return;
        }
        try {
            List<String> linhas = Files.readAllLines(arquivo);
            for (String linha : linhas) {
                if (linha.isBlank()) continue;          // pula linhas vazias
                Despesa d = Despesa.deCSV(linha);        // transforma a linha de volta em despesa
                despesas.add(d);
                // Garante que o próximo id seja sempre maior que o maior id já salvo,
                // para não repetir números.
                if (d.getId() >= proximoId) {
                    proximoId = d.getId() + 1;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️  Não consegui carregar o arquivo: " + e.getMessage());
        }
    }
}
