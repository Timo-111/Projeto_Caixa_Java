import java.util.ArrayList;
import java.util.List;

public class Venda {
    private List<Produto> itensVendidos;
    private double total;

    public Venda() {
        itensVendidos = new ArrayList<>();
        total = 0.0;
    }

    public void adicionarItem(Produto produto, int quantidade) {
        if (produto.getQuantidadeEstoque() >= quantidade) {
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
            itensVendidos.add(produto);
            total += produto.getPreco() * quantidade;
        }
    }

    public double calcularTotal() {
        return total;
    }

    public void finalizarVenda() {
        itensVendidos.clear();
        total = 0.0;
    }
}