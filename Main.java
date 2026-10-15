import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;

public class Main {


    private static final Scanner sc = new Scanner(System.in);

    private static final GerenciadorDespesas gerenciador = new GerenciadorDespesas();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        System.out.println("""
                ╔════════════════════════════════════════╗
                ║        💰  CONTROLE DE GASTOS  💰      ║
                ╚════════════════════════════════════════╝
                """);

        int opcao;
        do {
            menu();
            opcao = lerInteiro("Escolha uma opção: ");
            System.out.println();

            switch (opcao) {
                case 1 -> adicionar();
                case 2 -> listar();
                case 3 -> editar();
                case 4 -> remover();
                case 5 -> relatorio();
                case 0 -> System.out.println("👋 Até mais! Seus dados foram salvos.");
                default -> System.out.println("❌ Opção inválida. Tente novamente.");
            }
            System.out.println();
        } while (opcao != 0);
    }

    private static void menu() {
        System.out.println("────────────── MENU ──────────────");
        System.out.println("1 - Adicionar despesa");
        System.out.println("2 - Listar despesas");
        System.out.println("3 - Editar despesa");
        System.out.println("4 - Remover despesa");
        System.out.println("5 - Relatório (totais)");
        System.out.println("0 - Sair");
        System.out.println("───────────────────────────────────");
    }

    private static void adicionar() {
        System.out.println("➕ NOVA DESPESA");
        String descricao = lerTexto("Descrição: ");
        double valor = lerValor("Valor (R$): ");
        String categoria = lerTexto("Categoria (ex.: Alimentação, Transporte): ");
        LocalDate data = lerData("Data (dd/mm/aaaa) [Enter = hoje]: ");

        Despesa d = gerenciador.adicionar(descricao, valor, categoria, data);
        System.out.println("✅ Despesa adicionada! " + d);
    }

    private static void listar() {
        System.out.println("📋 SUAS DESPESAS");
        if (gerenciador.estaVazio()) {
            System.out.println("   (nenhuma despesa cadastrada ainda)");
            return;   // "return" sai do método aqui, pois não há o que listar.
        }
        for (Despesa d : gerenciador.listar()) {
            System.out.println("   " + d);   // o "+ d" usa o toString() da despesa
        }
        System.out.printf("%n   Total geral: R$ %.2f%n", gerenciador.totalGeral());
    }

    private static void editar() {
        System.out.println("✏️  EDITAR DESPESA");
        if (gerenciador.estaVazio()) {
            System.out.println("   (nenhuma despesa para editar)");
            return;
        }
        listar();
        int id = lerInteiro("%nID da despesa que deseja editar: ".formatted());
        if (gerenciador.buscarPorId(id) == null) {
            System.out.println("❌ Não achei uma despesa com esse ID.");
            return;
        }
        System.out.println("Digite os novos dados:");
        String descricao = lerTexto("Descrição: ");
        double valor = lerValor("Valor (R$): ");
        String categoria = lerTexto("Categoria: ");
        LocalDate data = lerData("Data (dd/mm/aaaa) [Enter = hoje]: ");

        gerenciador.atualizar(id, descricao, valor, categoria, data);
        System.out.println("✅ Despesa atualizada!");
    }

    private static void remover() {
        System.out.println("🗑️  REMOVER DESPESA");
        if (gerenciador.estaVazio()) {
            System.out.println("   (nenhuma despesa para remover)");
            return;
        }
        listar();
        int id = lerInteiro("%nID da despesa que deseja remover: ".formatted());
        if (gerenciador.remover(id)) {
            System.out.println("✅ Despesa removida!");
        } else {
            System.out.println("❌ Não achei uma despesa com esse ID.");
        }
    }

    private static void relatorio() {
        System.out.println("📊 RELATÓRIO");
        if (gerenciador.estaVazio()) {
            System.out.println("   (sem dados para o relatório)");
            return;
        }
        System.out.println("   Gastos por categoria:");
        Map<String, Double> porCategoria = gerenciador.totalPorCategoria();
        double total = gerenciador.totalGeral();

        for (Map.Entry<String, Double> e : porCategoria.entrySet()) {
            double pct = total > 0 ? (e.getValue() / total) * 100 : 0;
            System.out.printf("   • %-15s R$ %10.2f  (%4.1f%%)%n", e.getKey(), e.getValue(), pct);
        }
        System.out.printf("%n   💰 TOTAL GERAL: R$ %.2f%n", total);
    }

    private static String lerTexto(String rotulo) {
        System.out.print(rotulo);
        String txt = sc.nextLine().trim();
        while (txt.isEmpty()) {
            System.out.print("   Não pode ficar vazio. " + rotulo);
            txt = sc.nextLine().trim();
        }
        return txt;
    }

    private static int lerInteiro(String rotulo) {
        System.out.print(rotulo);
        while (true) {
            String txt = sc.nextLine().trim();
            try {
                return Integer.parseInt(txt);
            } catch (NumberFormatException e) {
                System.out.print("   Digite um número inteiro. " + rotulo);
            }
        }
    }

    private static double lerValor(String rotulo) {
        System.out.print(rotulo);
        while (true) {
            String txt = sc.nextLine().trim().replace(",", ".");
            try {
                double v = Double.parseDouble(txt);
                if (v < 0) {
                    System.out.print("   O valor não pode ser negativo. " + rotulo);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.print("   Digite um valor válido (ex.: 49.90). " + rotulo);
            }
        }
    }

    private static LocalDate lerData(String rotulo) {
        System.out.print(rotulo);
        while (true) {
            String txt = sc.nextLine().trim();
            if (txt.isEmpty()) {
                return LocalDate.now();   // Enter = hoje
            }
            try {
                return LocalDate.parse(txt, FMT);
            } catch (Exception e) {
                System.out.print("   Data inválida. Use dd/mm/aaaa. " + rotulo);
            }
        }
    }
}
