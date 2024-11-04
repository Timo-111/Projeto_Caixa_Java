package projetocaixagui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.io.Serializable;

public class Venda implements Serializable {
    private List<Produto> itensVendidos;
    private double total;
    private Date data;
    private String consNome, consCpf;

    public Venda() {
        itensVendidos = new ArrayList<>();
        total = 0.0;
    }

    public void adicionarItem(Produto produto) {
        if (produto.getQuantidadeEstoque() >0) {
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - 1);
            itensVendidos.add(produto);
            total += produto.getPreco();
        }
    }
    
    public boolean removerItem(Produto produto) {
        int locale = itensVendidos.indexOf(produto);
        if (locale!=-1) {
            itensVendidos.remove(locale);
            return false;
        }
        
        return true;
    }
    
    public String getNome() {
        return consNome;
    }
    
    public String getCpf() {
        return consCpf;
    }
    
    public void setNome(String nome) {
        this.consNome = (nome!=null) ? nome : "Desconhecido";
    }
    
    public void setCpf(String cpf) {
        this.consCpf = (cpf!=null) ? cpf : "000.000.000-00";
    }
    
    public void executarVenda() {
        data = new Date();
    }
    
    List<Produto> getItensVendidos(){
        return itensVendidos;
    }
    
    double getTotal() {
        return total;
    }
    
    Date getData() {
        return data;
    }

    public double calcularTotal() {
        return total;
    }

    
    
    public void finalizarVenda() {
        itensVendidos.clear();
        total = 0.0;
    }
}