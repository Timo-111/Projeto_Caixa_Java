import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Estoque implements Serializable {
    private List<Produto> produtos;

    public Estoque() {
        produtos = new ArrayList<>();
    }

    public void adicionarProduto(Produto produto) {
        produtos.add(produto);
    }

    public List<Produto> getProdutos() {
        return produtos;
    }
}