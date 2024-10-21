import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class ProjetoCaixaGUI {
    private Estoque estoque;
    private Venda venda;
    private JList<String> produtoList;
    private DefaultListModel<String> listModel;
    private JLabel valorCarrinhoLabel;
    private JPanel mainPanel;
    private JLabel logoLabel;
    private JPanel buttonPanel;
    private static final String ESTOQUE_FILE = "estoque.ser";

    public ProjetoCaixaGUI() {
        estoque = carregarEstoque();
        venda = new Venda();

        JFrame frame = new JFrame("Caixa de Lanchonete");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Logo da lanchonete
        logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        ImageIcon logoIcon = new ImageIcon("C:/Users/timo7/Desktop/projeto java facul/Projeto Caixa/images/logoMate.jpg"); // Caminho atualizado da imagem
        Image logoImage = logoIcon.getImage().getScaledInstance(500, 300, Image.SCALE_SMOOTH); // Redimensionar a imagem
        logoLabel.setIcon(new ImageIcon(logoImage));
        mainPanel.add(logoLabel, BorderLayout.CENTER);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3)); // Ajustado para 3 botões

        JButton listarButton = new JButton("Listar Produtos");
        listarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarOpcoesDeListagem();
            }
        });

        JButton adicionarButton = new JButton("Adicionar Produto");
        adicionarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarOpcoesAdicionarProduto();
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
        listarPanel.setLayout(new GridLayout(1, 3)); // Ajustado para 3 botões

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

        JButton docesButton = new JButton("Doces");
        docesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listarProdutos("Doces");
            }
        });

        listarPanel.add(bebidasButton);
        listarPanel.add(salgadosButton);
        listarPanel.add(docesButton); // Adicionado botão Doces

        mainPanel.removeAll();
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
        actionPanel.setLayout(new GridLayout(2, 2, 10, 10)); // Usar GridLayout para 2x2 botões com espaçamento

        valorCarrinhoLabel = new JLabel("Valor do Carrinho: R$ " + venda.calcularTotal()); // Manter o valor do carrinho

        // Redimensionar ícones
        ImageIcon addIcon = new ImageIcon(new ImageIcon("C:/Users/timo7/Desktop/projeto java facul/Projeto Caixa/images/add_icon.png").getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
        ImageIcon cartIcon = new ImageIcon(new ImageIcon("C:/Users/timo7/Desktop/projeto java facul/Projeto Caixa/images/cart_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        ImageIcon deleteIcon = new ImageIcon(new ImageIcon("C:/Users/timo7/Desktop/projeto java facul/Projeto Caixa/images/delete_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        ImageIcon backIcon = new ImageIcon(new ImageIcon("C:/Users/timo7/Desktop/projeto java facul/Projeto Caixa/images/back_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

        JButton adicionarCarrinhoButton = new JButton("Adicionar", addIcon);
        adicionarCarrinhoButton.setToolTipText("Adicionar ao Carrinho");
        adicionarCarrinhoButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        adicionarCarrinhoButton.setVerticalTextPosition(SwingConstants.CENTER);
        adicionarCarrinhoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adicionarAoCarrinho(categoria);
            }
        });

        JButton finalizarCompraButton = new JButton("Finalizar", cartIcon);
        finalizarCompraButton.setToolTipText("Finalizar Compra");
        finalizarCompraButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        finalizarCompraButton.setVerticalTextPosition(SwingConstants.CENTER);
        finalizarCompraButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                finalizarVenda();
            }
        });

        JButton removerProdutoButton = new JButton("Remover", deleteIcon);
        removerProdutoButton.setToolTipText("Remover Produto");
        removerProdutoButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        removerProdutoButton.setVerticalTextPosition(SwingConstants.CENTER);
        removerProdutoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removerProduto(categoria);
            }
        });

        JButton voltarButton = new JButton("Voltar", backIcon);
        voltarButton.setToolTipText("Voltar");
        voltarButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        voltarButton.setVerticalTextPosition(SwingConstants.CENTER);
        voltarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                voltarTelaInicial();
            }
        });

        actionPanel.add(adicionarCarrinhoButton);
        actionPanel.add(finalizarCompraButton);
        actionPanel.add(removerProdutoButton);
        actionPanel.add(voltarButton);

        listarProdutosPanel.add(listScrollPane, BorderLayout.CENTER);
        listarProdutosPanel.add(actionPanel, BorderLayout.SOUTH);

        mainPanel.removeAll();
        mainPanel.add(listarProdutosPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        mainPanel.revalidate();
        mainPanel.repaint();

        listModel.clear();
        for (Produto produto : estoque.getProdutos()) {
            if (produto.getCategoria().equals(categoria)) {
                listModel.addElement(produto.getNome());
            }
        }
    }

    private void mostrarOpcoesAdicionarProduto() {
        JPanel adicionarPanel = new JPanel();
        adicionarPanel.setLayout(new GridLayout(6, 2));

        JLabel categoriaLabel = new JLabel("Categoria:");
        String[] categorias = {"Bebidas", "Salgados", "Doces"};
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
                if (categoria.equals("Salgados") || categoria.equals("Doces")) {
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
                        estoque.adicionarProduto(new Produto(nome, preco, quantidade, categoria));
                    } else {
                        estoque.adicionarProduto(new Produto(nome, preco, Integer.MAX_VALUE, categoria)); // Quantidade infinita para salgados e doces
                    }

                    salvarEstoque();
                    voltarTelaInicial();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Por favor, insira valores válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton voltarButton = new JButton("Voltar");
        voltarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                voltarTelaInicial();
            }
        });

        adicionarPanel.add(categoriaLabel);
        adicionarPanel.add(categoriaComboBox);
        adicionarPanel.add(nomeLabel);
        adicionarPanel.add(nomeField);
        adicionarPanel.add(precoLabel);
        adicionarPanel.add(precoField);
        adicionarPanel.add(quantidadeLabel);
        adicionarPanel.add(quantidadeField);
        adicionarPanel.add(new JLabel()); // Placeholder
        adicionarPanel.add(adicionarButton);
        adicionarPanel.add(new JLabel()); // Placeholder
        adicionarPanel.add(voltarButton);

        mainPanel.removeAll();
        mainPanel.add(adicionarPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void voltarTelaInicial() {
        mainPanel.removeAll();
        mainPanel.add(logoLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Re-adiciona o painel de botões
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void adicionarAoCarrinho(String categoria) {
        String nome = produtoList.getSelectedValue();
        if (nome != null) {
            Produto produto = encontrarProdutoPorNome(nome);
            if (produto != null) {
                String quantidadeStr = JOptionPane.showInputDialog("Digite a quantidade:");
                try {
                    int quantidade = Integer.parseInt(quantidadeStr);
                    if (categoria.equals("Salgados") || categoria.equals("Doces") || produto.getQuantidadeEstoque() >= quantidade) {
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

    private void removerProduto(String categoria) {
        String nome = produtoList.getSelectedValue();
        if (nome != null) {
            Produto produto = encontrarProdutoPorNome(nome);
            if (produto != null) {
                estoque.getProdutos().remove(produto);
                salvarEstoque();
                listarProdutos(categoria);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum produto selecionado.");
        }
    }

    private void atualizarValorCarrinho() {
        valorCarrinhoLabel.setText("Valor do Carrinho: R$ " + venda.calcularTotal());
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

    private void salvarEstoque() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ESTOQUE_FILE))) {
            oos.writeObject(new ArrayList<>(estoque.getProdutos()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Estoque carregarEstoque() {
        Estoque estoque = new Estoque();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ESTOQUE_FILE))) {
            List<Produto> produtos = (List<Produto>) ois.readObject();
            for (Produto produto : produtos) {
                estoque.adicionarProduto(produto);
            }
        } catch (IOException | ClassNotFoundException e) {
            // Se o arquivo não existir ou houver um erro, retornamos um estoque vazio
        }
        return estoque;
    }

    public static void main(String[] args) {
        new ProjetoCaixaGUI();
    }
}