package projetocaixagui;

import java.io.Serializable;

public class Produto implements Serializable {
    private String nome;
    private double preco;
    private int quantidadeEstoque;
    private String categoria;
    private String marca;

    public Produto(String nome, double preco, int quantidadeEstoque, String marca, String categoria) {
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.marca = marca;
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }
    
    public String getMarca() {
        return marca;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public void setPreco(float novopreco) {
        this.preco = novopreco;
    }
    
    public void setMarca(String novamarca) {
        this.marca = novamarca;
    }
    
    public void setNome(String novonome) {
        this.nome = novonome;
    }
    
    public String getCategoria() {
        return categoria;
    }
}