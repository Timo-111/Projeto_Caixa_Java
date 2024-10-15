import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProjetoCaixaGUI {
    private Estoque estoque;
    private Venda venda;
    private JList<String> produtoList;
    private DefaultListModel<String> listModel;
    private JLabel valorCarrinhoLabel;
    private JPanel mainPanel;
    private JLabel logoLabel;

    public ProjetoCaixaGUI() {
        estoque = new Estoque();
        venda = new Venda();

        // Adicionando alguns produtos ao estoque
        estoque.adicionarProduto(new Produto("Refrigerante", 3.0, 20));
        estoque.adicionarProduto(new Produto("Suco", 4.0, 15));
        estoque.adicionarProduto(new Produto("Coxinha", 5.0, Integer.MAX_VALUE)); // Salgado com estoque infinito

        JFrame frame = new JFrame("Caixa de Lanchonete");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Logo da lanchonete
        logoLabel = new JLabel("Logo da Lanchonete", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(logoLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        JButton listarButton = new JButton("Listar Produtos");
        listarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarOpcoesDeListagem();
            }
        });

        JButton adicionarButton = new JButton("Adicionar Produto");
        adicionarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirJanelaAdicionarProduto();
            }
        });

        JButton finalizarButton = new JButton("Finalizar Venda");
        finalizarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                finalizarVenda();
            }
        });

        buttonPanel.add(listarButton);
        buttonPanel.add(adicionarButton);
        buttonPanel.add(finalizarButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    private void mostrarOpcoesDeListagem() {
        JPanel listarPanel = new JPanel();
        listarPanel.setLayout(new GridLayout(1, 2));

        JButton bebidasButton = new JButton("Bebidas");
        bebidasButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listarProdutos("Bebidas");
            }
        });

        JButton salgadosButton = new JButton("Salgados");
        salgadosButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listarProdutos("Salgados");
            }
        });

        listarPanel.add(bebidasButton);
        listarPanel.add(salgadosButton);

        mainPanel.remove(logoLabel);
        mainPanel.add(listarPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void listarProdutos(String categoria) {
        JPanel listarProdutosPanel = new JPanel();
        listarProdutosPanel.setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        produtoList = new JList<>(listModel);
        produtoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(produtoList);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(3, 1));

        valorCarrinhoLabel = new JLabel("Valor do Carrinho: R$ 0.0");
        JButton adicionarCarrinhoButton = new JButton("Adicionar ao Carrinho");
        adicionarCarrinhoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adicionarAoCarrinho(categoria);
            }
        });

        JButton finalizarCompraButton = new JButton("Finalizar Compra");
        finalizarCompraButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                finalizarVenda();
            }
        });

        actionPanel.add(valorCarrinhoLabel);
        actionPanel.add(adicionarCarrinhoButton);
        actionPanel.add(finalizarCompraButton);

        listarProdutosPanel.add(listScrollPane, BorderLayout.CENTER);
        listarProdutosPanel.add(actionPanel, BorderLayout.SOUTH);

        mainPanel.removeAll();
        mainPanel.add(listarProdutosPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        mainPanel.revalidate();
        mainPanel.repaint();

        listModel.clear();
        for (Produto produto : estoque.getProdutos()) {
            if ((categoria.equals("Bebidas") && produto.getQuantidadeEstoque() != Integer.MAX_VALUE) ||
                (categoria.equals("Salgados") && produto.getQuantidadeEstoque() == Integer.MAX_VALUE)) {
                listModel.addElement(produto.getNome());
            }
        }
    }

    private void adicionarAoCarrinho(String categoria) {
        String nome = produtoList.getSelectedValue();
        if (nome != null) {
            Produto produto = encontrarProdutoPorNome(nome);
            if (produto != null) {
                String quantidadeStr = JOptionPane.showInputDialog("Digite a quantidade:");
                try {
                    int quantidade = Integer.parseInt(quantidadeStr);
                    if (categoria.equals("Salgados") || produto.getQuantidadeEstoque() >= quantidade) {
                        venda.adicionarItem(produto, quantidade);
                        if (categoria.equals("Bebidas")) {
                            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
                        }
                        atualizarValorCarrinho();
                    } else {
                        JOptionPane.showMessageDialog(null, "Quantidade em estoque insuficiente.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, insira uma quantidade válida.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum produto selecionado.");
        }
    }

    private void atualizarValorCarrinho() {
        valorCarrinhoLabel.setText("Valor do Carrinho: R$ " + venda.calcularTotal());
    }

    private void abrirJanelaAdicionarProduto() {
        JFrame adicionarFrame = new JFrame("Adicionar Produto");
        adicionarFrame.setSize(300, 200);
        adicionarFrame.setLayout(new GridLayout(5, 2));

        JLabel categoriaLabel = new JLabel("Categoria:");
        String[] categorias = {"Bebidas", "Salgados"};
        JComboBox<String> categoriaComboBox = new JComboBox<>(categorias);

        JLabel nomeLabel = new JLabel("Nome:");
        JTextField nomeField = new JTextField();

        JLabel precoLabel = new JLabel("Preço:");
        JTextField precoField = new JTextField();

        JLabel quantidadeLabel = new JLabel("Quantidade:");
        JTextField quantidadeField = new JTextField();

        // Adicionar ActionListener para o JComboBox
        categoriaComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String categoria = (String) categoriaComboBox.getSelectedItem();
                if (categoria.equals("Salgados")) {
                    quantidadeLabel.setVisible(false);
                    quantidadeField.setVisible(false);
                } else {
                    quantidadeLabel.setVisible(true);
                    quantidadeField.setVisible(true);
                }
            }
        });

        JButton adicionarButton = new JButton("Adicionar");
        adicionarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String categoria = (String) categoriaComboBox.getSelectedItem();
                    String nome = nomeField.getText();
                    double preco = Double.parseDouble(precoField.getText());

                    if (categoria.equals("Bebidas")) {
                        int quantidade = Integer.parseInt(quantidadeField.getText());
                        estoque.adicionarProduto(new Produto(nome, preco, quantidade));
                    } else {
                        estoque.adicionarProduto(new Produto(nome, preco, Integer.MAX_VALUE)); // Quantidade infinita para salgados
                    }

                    adicionarFrame.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(adicionarFrame, "Por favor, insira valores válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        adicionarFrame.add(categoriaLabel);
        adicionarFrame.add(categoriaComboBox);
        adicionarFrame.add(nomeLabel);
        adicionarFrame.add(nomeField);
        adicionarFrame.add(precoLabel);
        adicionarFrame.add(precoField);
        adicionarFrame.add(quantidadeLabel);
        adicionarFrame.add(quantidadeField);
        adicionarFrame.add(new JLabel()); // Placeholder
        adicionarFrame.add(adicionarButton);

        adicionarFrame.setVisible(true);
    }

    private void finalizarVenda() {
        JOptionPane.showMessageDialog(null, "Venda finalizada. Total: R$ " + venda.calcularTotal());
        venda.finalizarVenda();
        atualizarValorCarrinho();
    }

    private Produto encontrarProdutoPorNome(String nome) {
        for (Produto produto : estoque.getProdutos()) {
            if (produto.getNome().equals(nome)) {
                return produto;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        new ProjetoCaixaGUI();
    }
}