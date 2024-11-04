package projetocaixagui;

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
    
    public boolean apagarProduto(Produto prod) {
        int ind = produtos.indexOf(prod);

        if (ind!=-1) {
            produtos.remove(ind);
        }
        return false;
    }
    
    public Produto existe(String nome) {
        Produto buffer = null;
        
        for (int i=0;i<produtos.size();i++) {
            if (produtos.get(i).getNome().equals(nome)) {
                buffer = produtos.get(i);
                break;
            }
        }
        
        return buffer;
    }
}