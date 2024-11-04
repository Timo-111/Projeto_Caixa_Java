package projetocaixagui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ProjetoCaixaGUI {
    private Estoque estoque;
    private Venda venda;
    private JList<String> produtoList;
    private List<Venda> vendas;
    
    private DefaultListModel<String> listModel;
    private DefaultListModel<String> vendaCatListModel;
    private DefaultListModel<String> vendaSelListModel;
    private DefaultListModel<Produto> vendaSelProdListModel;
    
    private Venda vendaSelecionada;
    private JList<String> vendasStringJList;
    private DefaultListModel<String> vendasStringList;
    private JList<String> itensVendStringJList;
    private DefaultListModel<String> itensVendStringList;

    
    private DefaultListModel<String> vendaProdutosSel;
    
    private JLabel valorCarrinhoLabel;
    private JPanel mainPanel;
    private JDialog estoqueInputPanel;
    private JLabel logoLabel;
    private JPanel buttonPanel;
    private static final String ESTOQUE_FILE = "estoque.ser";
    private static final String VENDA_FILE = "venda.ser";
    
    private static String pastaimgBUFFER;
    private static String pasta = ".";
    private static File pastaimg;
    
    private static Produto produtoSelecionado = null;
    
    private static conexaoMysql database = new conexaoMysql();

    private  void carregarCatLista(DefaultListModel<String> lista, String categoria) {
        for (Produto produto : estoque.getProdutos()) {
            //System.out.println(produto.getNome());
            if (produto.getCategoria().equals(categoria)) {
                lista.addElement(produto.getNome());
                
            }
        }
    }
    
    public ProjetoCaixaGUI() {
        pastaimgBUFFER = new File("").getAbsolutePath()+"/Projeto Caixa/images";
        
        File pastaimg = new File(pastaimgBUFFER);
        pasta = pastaimg.getAbsolutePath();
        
        if (!pastaimg.exists()) {            
            JOptionPane.showMessageDialog(null, "A pasta \""+pasta+"\" não existe!\n\nIsso não fará o programa cessar o comportamento padrão","ERRO!", JOptionPane.ERROR_MESSAGE);
            
        }
        
       
        
        
        estoque = carregarEstoque();
        vendas = carregarVendas();

        JFrame frame = new JFrame("Caixa de Lanchonete");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Logo da lanchonete
        logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        ImageIcon logoIcon = new ImageIcon(pasta+"\\logoMate.jpg"); // Caminho atualizado da imagem
        Image logoImage = logoIcon.getImage().getScaledInstance(500, 300, Image.SCALE_SMOOTH); // Redimensionar a imagem
        logoLabel.setIcon(new ImageIcon(logoImage));
        mainPanel.add(logoLabel, BorderLayout.CENTER);

        //DECLARACAO DE BUTTONS, BOTOES
        
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4)); // Ajustado para 3 botões

        
        ImageIcon listIcon = new ImageIcon( new ImageIcon(pasta+"\\list_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        JButton listarButton = new JButton("Listagem",listIcon); //listar produtos{atualizar estoque},dias e vendas do dia
        listarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarOpcoesDeListagem();
            }
        });

        ImageIcon plusIcon = new ImageIcon( new ImageIcon(pasta+"\\add_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        JButton adicionarButton = new JButton("Adicionar Novo Produto",plusIcon); //adicionar produto, editar produto no estoque
        adicionarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarOpcoesAdicionarProduto();
            }
        });

        ImageIcon cartIcon = new ImageIcon(new ImageIcon(pasta+"\\cart_icon.png").getImage().getScaledInstance(24,24,Image.SCALE_SMOOTH));
        JButton finalizarButton = new JButton("Nova Venda",cartIcon); //nova lista
        finalizarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                novaVenda();
            }
        });
        
        /*JButton datasVendasButton = new JButton("Datas");
        datasVendasButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               datasVendasfun();
           } 
        });*/


        buttonPanel.add(listarButton);
        buttonPanel.add(adicionarButton);
        buttonPanel.add(finalizarButton);
        //buttonPanel.add(datasVendasButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    //botão "Listar Produtos", parte 1
    private void mostrarOpcoesDeListagem() {
        JPanel listarPanel = new JPanel();
        listarPanel.setLayout(new GridLayout(5, 0)); // Ajustado para 3 botões

        JButton listagemEstoque = new JButton("Lista de Produtos");
        listagemEstoque.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               listProdSelCat();
           } ;
        });
        
        JPanel padding1 = new JPanel();
        JPanel padding2 = new JPanel();
        //padding.setBorder(new EmptyBorder(10,10,10,10));
        
        JButton listagemDatas = new JButton("Lista de Datas e Vendas");
        listagemDatas.addActionListener(new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                listarVendas();
            }
        });
        
        listarPanel.add(listagemEstoque);
        listarPanel.add(padding1);
        listarPanel.add(listagemDatas);
        
        ImageIcon backIcon = new ImageIcon(new ImageIcon(pasta+"\\back_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        JButton voltarButton = new JButton("Voltar", backIcon);
        voltarButton.setToolTipText("Voltar");
        voltarButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        voltarButton.setVerticalTextPosition(SwingConstants.CENTER);
        voltarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                voltarTelaInicial();
            }
        });
        
        listarPanel.add(padding2);
        listarPanel.add(voltarButton);
        
        /*JButton bebidasButton = new JButton("Bebidas");
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
        */
        
        mainPanel.removeAll();
        mainPanel.add(listarPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    
    private void listProdSelCat() {
        JPanel listarPanel = new JPanel();
        listarPanel.setLayout(new GridLayout(3, 3));
        
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
        listarPanel.add(docesButton);
        
        mainPanel.removeAll();
        mainPanel.add(listarPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    //botão "Listar Produtos", parte 2
    private void listarProdutos(String categoria) {
        JPanel listarProdutosPanel = new JPanel();
        
        JPanel infosProdutos = new JPanel();
        infosProdutos.setLayout(new GridLayout(2,1));
        
        listarProdutosPanel.setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        produtoList = new JList<>(listModel);
        produtoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScrollPane = new JScrollPane(produtoList);
        listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); //para termos certeza de que pode rolar pra baixo sempre

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(2, 2, 1, 1)); // Usar GridLayout para 2x2 botões com espaçamento
        JPanel voltarButtonPanel = new JPanel();
        voltarButtonPanel.setLayout(new GridLayout(2,0));

        //valorCarrinhoLabel = new JLabel("Valor do Carrinho: R$ " + venda.calcularTotal()); // Manter o valor do carrinho

        JButton nome, marca, preco, qtd;
        //JLabel nome, marca, preco, qtd;
        nome = new JButton("Nome: ");
        marca = new JButton("Marca: ");
        preco = new JButton("Preço: ");
        qtd = new JButton("Quantidade em Estoque: ");
        
        infosProdutos.add(nome);
        infosProdutos.add(marca);
        infosProdutos.add(preco);
        infosProdutos.add(qtd);
        
        nome.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                String nBuffer = JOptionPane.showInputDialog("Insira o novo nome do produto");
                if (nBuffer!=null) {
                    produtoSelecionado.setNome(nBuffer);
                    nome.setText("Nome: "+nBuffer);
                    salvarEstoque();

                    listModel.clear();
                    //System.out.println("\n"+categoria+"\n");
                    carregarCatLista(listModel,categoria);
                }
            }
        });
        
        preco.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               String npBuffer = JOptionPane.showInputDialog("Insira o novo preço:");
               if (npBuffer!=null) {
                npBuffer = npBuffer.replace(',', '.'); //previnir erros de lógica
                npBuffer = npBuffer.replace("R$", "");

                float novopreco= (float)12.34;
                try {
                 novopreco = Float.parseFloat(npBuffer);

                 produtoSelecionado.setPreco(novopreco);

                 preco.setText(String.format("Preço: R$%.2f", novopreco));

                 salvarEstoque();
                } catch (NumberFormatException xc) {
                    JOptionPane.showMessageDialog(null, "Insira um número decimal válido.","ERRO!", JOptionPane.ERROR_MESSAGE);
                }

               }
           } 
        });
        
        qtd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //System.out.println(produtoSelecionado);
                
                //System.out.println(estoqueInputPanel);
                
                if (!produtoSelecionado.getCategoria().equals("Bebidas")) {
                    JOptionPane.showMessageDialog(null, "É IMPOSSÍVEL alterar o estoque de doces ou salgados, pois eles são preparados pelo própio estabelecimento.","ERRO!", JOptionPane.ERROR_MESSAGE);
                } else {
                    estoqueInputPanel = new JDialog();
                    estoqueInputPanel.setTitle("Alterar informação de estoque");
                    estoqueInputPanel.setSize(new Dimension(320,128));
                    //estoqueInputPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    estoqueInputPanel.setLayout(new GridLayout(2,2));
                    JLabel numLabel = new JLabel("Insira um novo número: ");
                    JTextField numInput = new JTextField();
                    
                    JButton somEstoque = new JButton("SOMAR ESTOQUE");
                    
                    somEstoque.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) {
                           try {
                            int inp = Integer.parseInt(numInput.getText());
                            int est = produtoSelecionado.getQuantidadeEstoque();
                            produtoSelecionado.setQuantidadeEstoque(inp+est);
                            qtd.setText("Quantidade em Estoque: "+String.valueOf(inp+est));
                            salvarEstoque();
                            //estoqueInputPanel.dispatchEvent(new WindowEvent(estoqueInputPanel, WindowEvent.WINDOW_CLOSING));
                           } catch (NumberFormatException xc) {
                               JOptionPane.showMessageDialog(mainPanel, "Por favor, insira valores válidos.", "ERRO!", JOptionPane.ERROR_MESSAGE);
                           }
                           finally {
                               estoqueInputPanel.setVisible(false);
                           }
                       } 
                    });
                    
                    JButton altEstoque = new JButton("ALTERAR ESTOQUE");
                    
                    altEstoque.addActionListener(new ActionListener() {
                       public void actionPerformed(ActionEvent e) {
                           try {
                            int inp = Integer.parseInt(numInput.getText());
                            produtoSelecionado.setQuantidadeEstoque(inp);
                            qtd.setText("Quantidade em Estoque: "+String.valueOf(inp));
                            salvarEstoque();
                            //estoqueInputPanel.dispatchEvent(new WindowEvent(estoqueInputPanel, WindowEvent.WINDOW_CLOSING));
                           } catch (NumberFormatException xc) {
                               JOptionPane.showMessageDialog(mainPanel, "Por favor, insira valores válidos.", "ERRO!", JOptionPane.ERROR_MESSAGE);
                           }
                           finally {
                               estoqueInputPanel.setVisible(false);
                           }
                       } 
                    });
                    
                    
                    
                    estoqueInputPanel.add(numLabel);
                    estoqueInputPanel.add(numInput);
                    
                    estoqueInputPanel.add(somEstoque);
                    estoqueInputPanel.add(altEstoque);
                    
                    
                    estoqueInputPanel.setVisible(true);
                }
            }
        });
        
        marca.addActionListener(new ActionListener() { 
            public void actionPerformed(ActionEvent e) {
                String nmBuffer = JOptionPane.showInputDialog("Insira o novo nome da marca");
                if (nmBuffer!=null) {
                produtoSelecionado.setMarca(nmBuffer);
                
                marca.setText("Marca: "+nmBuffer);
                salvarEstoque();
                }
            }
        });
        
        //listarProdutosPanel.add(infosProdutos);
        
        // Redimensionar ícones
        /*ImageIcon addIcon = new ImageIcon(new ImageIcon(pasta+"\\add_icon.png").getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
        ImageIcon cartIcon = new ImageIcon(new ImageIcon(pasta+"\\cart_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        ImageIcon deleteIcon = new ImageIcon(new ImageIcon(pasta+"\\delete_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        ImageIcon backIcon = new ImageIcon(new ImageIcon(pasta+"\\back_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        */

/*
        //botão "Adicionar"
        JButton adicionarCarrinhoButton = new JButton("Adicionar", addIcon);
        adicionarCarrinhoButton.setToolTipText("Adicionar ao Carrinho");
        adicionarCarrinhoButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        adicionarCarrinhoButton.setVerticalTextPosition(SwingConstants.CENTER);
        adicionarCarrinhoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adicionarAoCarrinho(categoria);
            }
        });

        //botão "Finalizar"
        JButton finalizarCompraButton = new JButton("Finalizar", cartIcon);
        finalizarCompraButton.setToolTipText("Finalizar Compra");
        finalizarCompraButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        finalizarCompraButton.setVerticalTextPosition(SwingConstants.CENTER);
        finalizarCompraButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                finalizarVenda();
            }
        });

        //botão "Remover"
        JButton removerProdutoButton = new JButton("Remover", deleteIcon);
        removerProdutoButton.setToolTipText("Remover Produto");
        removerProdutoButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        removerProdutoButton.setVerticalTextPosition(SwingConstants.CENTER);
        removerProdutoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removerProduto(categoria);
            }
        });

        //botão "Voltar"
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
        
        */

        ImageIcon backIcon = new ImageIcon(new ImageIcon(pasta+"\\back_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        JButton voltarButton = new JButton("Voltar", backIcon);
        voltarButton.setToolTipText("Voltar");
        voltarButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        voltarButton.setVerticalTextPosition(SwingConstants.CENTER);
        voltarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                voltarTelaInicial();
            }
        });

        JButton apagarButton = new JButton("Apagar Produto");
        apagarButton.setToolTipText("Apagar Produto");
        apagarButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        apagarButton.setVerticalTextPosition(SwingConstants.CENTER);
        apagarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Produto prodApagBuff = estoque.existe(produtoList.getSelectedValue());
                if (prodApagBuff!=null) {
                int sobrescrever = JOptionPane.showConfirmDialog(null,"Deseja apagar o produto "+prodApagBuff.getNome()+" para sempre?","Apagar",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                if (sobrescrever == JOptionPane.YES_OPTION) {
                        estoque.apagarProduto(prodApagBuff);
                    listModel.clear();
                    carregarCatLista(listModel,categoria);
                    salvarEstoque();
                    //apagarProduto();
                        return;
                    }
                }
            }
        });
        
        listarProdutosPanel.add(listScrollPane, BorderLayout.CENTER);
        listarProdutosPanel.add(infosProdutos, BorderLayout.SOUTH);

        voltarButtonPanel.add(apagarButton,BorderLayout.SOUTH);
        voltarButtonPanel.add(voltarButton,BorderLayout.SOUTH);
        
        
        mainPanel.removeAll();
        mainPanel.add(listarProdutosPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        mainPanel.add(voltarButtonPanel,BorderLayout.SOUTH);
        mainPanel.revalidate();
        mainPanel.repaint();

        listModel.clear();
        carregarCatLista(listModel,categoria);
        /*for (Produto produto : estoque.getProdutos()) {
            if (produto.getCategoria().equals(categoria)) {
                listModel.addElement(produto.getNome());
                
            }
        }*/
        
        produtoList.addListSelectionListener(new ListSelectionListener() {;
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting() && !produtoList.isSelectionEmpty()) { //previne o codigo de ser executado 2 vezes
                        String selBuffer = produtoList.getSelectedValue();//.toString();
                        //System.out.println(selBuffer);
                        
                        Produto buffer = estoque.existe(selBuffer);
                        
                        if (buffer != null) {
                            produtoSelecionado = buffer;
                            nome.setText("Nome: "+buffer.getNome());
                            marca.setText( (buffer.getCategoria().equals("Bebidas")) ? ("Marca: "+buffer.getMarca()) : "Produto caseiro" );
                            preco.setText(String.format("Preço: R$%.2f",buffer.getPreco()));
                            qtd.setText( (buffer.getCategoria().equals("Bebidas")) ? ("Quantidade em Estoque: "+buffer.getQuantidadeEstoque()) : "Produto preparado" );
                            
                        } else {
                             nome.setText("Não foi possível retornar o nome do produto.");
                            marca.setText("Não foi possível retornar a marca do produto.");
                            preco.setText("Não foi possível retornar o preço do produto.");
                            qtd.setText("Não foi possível retornar a marca do produto.");

                        }
                    }
                }
        });
    }

    private void listarVendas(){
        JPanel listarVendasPanel = new JPanel();
        JPanel infoVendasPanel = new JPanel();
        JPanel botoesPanel = new JPanel();
        
        botoesPanel.setLayout(new GridLayout(3,1));
        infoVendasPanel.setLayout(new GridLayout(3,1));
        listarVendasPanel.setLayout(new BorderLayout());
        //infoVendasPanel
        
        int selIndVend;
        
        vendasStringList = new DefaultListModel<>();
        vendasStringJList = new JList<>(vendasStringList);
        vendasStringJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane vendalistScrollPane = new JScrollPane(vendasStringJList);
        vendalistScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        itensVendStringList = new DefaultListModel<>();
        itensVendStringJList = new JList<>(itensVendStringList);
        itensVendStringJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane itensVendScrollPane = new JScrollPane(itensVendStringJList);
        itensVendScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        
        JLabel info = new JLabel("<html>Nome do cliente: <br>CPF do cliente: <br>Data da venda: 01/01/2000 às 00:00<br>Total: R$0,00<br>Itens vendidos:</html>");
        
        vendasStringList.clear();
        for (int i=0;i<vendas.size();i++) {
            String buffDataVen = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(vendas.get(i).getData());
            String buffStrVenda = vendas.get(i).getNome()+" - "+vendas.get(i).getCpf()+" - "+buffDataVen;
            vendasStringList.addElement(buffStrVenda);
        }
        
        ImageIcon backIcon = new ImageIcon(new ImageIcon(pasta+"\\back_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        JButton voltarButton = new JButton("Voltar", backIcon);
        voltarButton.setToolTipText("Voltar");
        voltarButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        voltarButton.setVerticalTextPosition(SwingConstants.CENTER);
        voltarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                voltarTelaInicial();
            }
        });
        
        //ImageIcon cartIcon = new ImageIcon(new ImageIcon(pasta+"\\cart_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        JButton apagarButton = new JButton("Apagar");//, cartIcon);
        apagarButton.setToolTipText("Apagar");
        apagarButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        apagarButton.setVerticalTextPosition(SwingConstants.CENTER);
        apagarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                apagarRegVenda(vendasStringJList.getSelectedIndex(),vendasStringList);
                salvarVendas();
            }
        });
        
        JButton salvarTxtButton = new JButton("Salvar nota fiscal");//, cartIcon);
        salvarTxtButton.setToolTipText("Apagar");
        salvarTxtButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        salvarTxtButton.setVerticalTextPosition(SwingConstants.CENTER);
        salvarTxtButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                
                
                salvarNotaFiscal();
            }
        });
        
        vendasStringJList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && !vendasStringJList.isSelectionEmpty()) {
                    String selBuffer = vendasStringJList.getSelectedValue();
                    int selInd = vendasStringJList.getSelectedIndex();
                    //System.out.println(selBuffer);
                    //System.out.println(selInd);
                    
                    Venda selVenda = vendas.get(selInd);
                    venda = selVenda;
                    
                    String buffDataVen = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(vendas.get(selInd).getData());
                    
                    info.setText(String.format("<html>Nome do cliente: %s<br>CPF do cliente: %s<br>Data da venda: %s<br>Total: R$%.2f<br>Itens vendidos:</html>",selVenda.getNome(),selVenda.getCpf(), buffDataVen, selVenda.getTotal() ));
                    
                    //DefaultListModel<Produto> produtosVendidos = new DefaultListModel<>();//(vendas.get(selInd).getItensVendidos());
                    itensVendStringList.clear();
                    for (Produto prod : vendas.get(selInd).getItensVendidos()) {
                        itensVendStringList.addElement(prod.getNome());
                    }
                }
            }
        });
        
       /*vendasStringJList.addListSelectionListener(new ListSelectionListener(){
           public void valueChanged(ListSelectionEvent e) {
                    
                }
       }*/
        
        infoVendasPanel.add(info, BorderLayout.NORTH);
        infoVendasPanel.add(itensVendScrollPane, BorderLayout.CENTER);
        botoesPanel.add(salvarTxtButton, BorderLayout.SOUTH);
        botoesPanel.add(apagarButton, BorderLayout.SOUTH);
        botoesPanel.add(voltarButton, BorderLayout.SOUTH);
        infoVendasPanel.add(botoesPanel);
        
        mainPanel.removeAll();
        mainPanel.add(vendalistScrollPane, BorderLayout.CENTER);
        mainPanel.add(infoVendasPanel, BorderLayout.WEST);
        //mainPanel.add(voltarButtonPanel,BorderLayout.SOUTH);
        mainPanel.revalidate();
        mainPanel.repaint();

        //listModel.clear();
        
    }
    
    private void salvarNotaFiscal() {
        if (venda!=null) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar nota fiscal");
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Arquivos de texto (*.txt)","txt");
        fileChooser.setFileFilter(filter);
        
        String defFname = venda.getNome()+" - ";
        String buffDataVenT = new SimpleDateFormat("dd-MM-yyyy - HH-mm-ss").format(venda.getData());
        defFname=defFname+buffDataVenT+".txt";
        
        fileChooser.setSelectedFile(new File(defFname));
        
        int userSel = fileChooser.showSaveDialog(null);
        
        if (userSel == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            String fName = file.getName();
            //System.out.println(fName);
            
            if (!fName.endsWith(".txt")) {
                file = new File(file.getAbsolutePath()+".txt");
            }
            
            if (file.exists()) {
                int sobrescrever = JOptionPane.showConfirmDialog(null,"O arquivo selecionado já existe, deseja sobrescrever?","Confirmar subscrição",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                if (sobrescrever != JOptionPane.YES_OPTION) {
                    //System.out.println("Arquivo cancelado.");
                    return;
                }
            }
            
            fName = file.getName();
            //System.out.println(fName);
            
            try (FileWriter fWriter = new FileWriter(file)) {
                String contArq;
                String titulo = "MATE GUARANÁ\nDOCES E SALGADOS\nNOTA FISCAL\n-------------------------\n";
                String buffDataVen = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(venda.getData());
                contArq=titulo+buffDataVen;
                contArq=contArq+"\nNome do Cliente: "+venda.getNome()+"\nCPF do Cliente: "+venda.getCpf()+"\n-------------------------\n";
                
                float precoProdTotal=0;
                
                int occurances=0;
                DefaultListModel<String> listed = new DefaultListModel();
                String target;

                vendaProdutosSel = new DefaultListModel<>();
                
                for (Produto prod : venda.getItensVendidos()) {
                    vendaProdutosSel.addElement(prod.getNome());
                }
                
                vendaSelListModel = new DefaultListModel<>();
                //vendaProdutosSel = new DefaultListModel<>();

                
                for (int i=0;i<vendaProdutosSel.size();i++) {
                    if (listed.indexOf(vendaProdutosSel.get(i))==-1) {
                        occurances=0;
                        target=vendaProdutosSel.get(i);
                        for (int j=0;j<vendaProdutosSel.size();j++) {
                            if (vendaProdutosSel.get(j).equals(target)) {
                                occurances++;
                            }
                        }
                        float precoProd = 0;
                        Produto prodBuff = estoque.existe(target);
                        if (prodBuff!=null) {
                            //System.out.println(prodBuff.getPreco());
                            precoProd=(float) (prodBuff.getPreco()*occurances);
                            //System.out.println(precoProd);
                        }
                        vendaSelListModel.addElement(String.format("%s %dx - R$%.2f",target,occurances,precoProd));
                        precoProdTotal+=precoProd;
                        listed.addElement(target);
                    }
                }
                
                for (int i=0;i<vendaSelListModel.size();i++) {
                    //System.out.println(vendaSelListModel.get(i));
                    contArq=contArq+"\n"+vendaSelListModel.get(i);
                }
                contArq=contArq+String.format("\n-------------------------\nTotal: R$%.2f",precoProdTotal);
                
                
                fWriter.write(contArq);
                //System.out.println("Arquivo salvo: {"+file.getAbsolutePath()+"}");
            } catch (IOException e) { e.printStackTrace(); }
            
        }
        }
    }
    
    private void apagarRegVenda(int ind, DefaultListModel<String> lis) {
        if (ind>-1) {
            //System.out.println(lis.get(ind));
            lis.remove(ind);
        }
    }
    
    //botão "Adicionar Produto"
    private void mostrarOpcoesAdicionarProduto() {
        JPanel adicionarPanel = new JPanel();
        adicionarPanel.setLayout(new GridLayout(7,2));

        JLabel categoriaLabel = new JLabel("Categoria:");
        String[] categorias = {"Bebidas", "Salgados", "Doces"};
        JComboBox<String> categoriaComboBox = new JComboBox<>(categorias);

        JLabel nomeLabel = new JLabel("Nome: ");
        JTextField nomeField = new JTextField();

        JLabel precoLabel = new JLabel("Preço: ");
        JTextField precoField = new JTextField();
        
        JLabel marcaLabel = new JLabel("Marca: ");
        JTextField marcaField = new JTextField();

        JLabel quantidadeLabel = new JLabel("Quantidade:");
        JTextField quantidadeField = new JTextField();

        // Adicionar ActionListener para o JComboBox
        categoriaComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String categoria = (String) categoriaComboBox.getSelectedItem();
                //salgados e doces serão prontos, não terceirizados
                if (categoria.equals("Salgados") || categoria.equals("Doces")) {
                    quantidadeLabel.setVisible(false);
                    quantidadeField.setVisible(false);
                    
                    marcaLabel.setVisible(false);
                    marcaField.setVisible(false);
                } else {
                    quantidadeLabel.setVisible(true);
                    quantidadeField.setVisible(true);
                    
                    marcaLabel.setVisible(true);
                    marcaField.setVisible(true);
                }
            }
        });

        JButton adicionarButton = new JButton("Adicionar");
        adicionarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String categoria = (String) categoriaComboBox.getSelectedItem();
                    String nome = nomeField.getText();
                    if (nome == null | nome.isEmpty()) {
                        throw new RuntimeException("O nome não pode ser vazio!");
                    }
                    
                    if (estoque.existe(nome)!=null) {
                        throw new RuntimeException("Já existe um produto com este nome!");
                    }
                    String marca = marcaField.getText();
                    String prBuffer = precoField.getText();
                    prBuffer = prBuffer.replace(',', '.');
                    prBuffer = prBuffer.replace("R$", "");
                    
                    double preco = Double.parseDouble(prBuffer);

                    if (categoria.equals("Bebidas")) {
                        int quantidade = Integer.parseInt(quantidadeField.getText());
                        estoque.adicionarProduto(new Produto(nome, preco, quantidade, marca, categoria));
                    } else {
                        estoque.adicionarProduto(new Produto(nome, preco, Integer.MAX_VALUE, "Produto caseiro", categoria)); // Quantidade infinita para salgados e doces
                    }

                    salvarEstoque();
                    voltarTelaInicial();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Por favor, insira valores válidos.", "ERRO!", JOptionPane.ERROR_MESSAGE);
                }
                catch (RuntimeException ex) {
                    JOptionPane.showMessageDialog(mainPanel, ex.getMessage() , "ERRO!", JOptionPane.ERROR_MESSAGE);
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
        adicionarPanel.add(marcaLabel);
        adicionarPanel.add(marcaField);
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

    /* função de venda antiga
    private void adicionarAoCarrinho(String categoria) {
        String nome = produtoList.getSelectedValue();
        if (nome != null) {
            Produto produto = encontrarProdutoPorNome(nome);
            if (produto != null) {
                String quantidadeStr = JOptionPane.showInputDialog("Digite a quantidade:");
                try {
                    int quantidade = Integer.parseInt(quantidadeStr);
                    if (categoria.equals("Salgados") || categoria.equals("Doces") || produto.getQuantidadeEstoque() >= quantidade) {
                        venda.adicionarItem(produto, quantidade); //ARRAIA VENDA
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
    */
    
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

    private void novaVenda() {
        vendaProdutosSel = new DefaultListModel<>();
        vendaProdutosSel.clear();
        
        JPanel vendaPanel = new JPanel();
        
        JPanel catlistPanel = new JPanel();
        JPanel itlistPanel = new JPanel ();
        JPanel selCatPanel = new JPanel();
        selCatPanel.setLayout(new GridLayout(0,1));
        
        ImageIcon backIcon = new ImageIcon(new ImageIcon(pasta+"\\back_icon.png").getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        ImageIcon sellIcon = new ImageIcon(new ImageIcon(pasta+"\\cart_icon.png").getImage().getScaledInstance(24,24,Image.SCALE_SMOOTH));

        JButton execVenda = new JButton("Executar Venda", sellIcon);
        
        catlistPanel.setLayout(new BorderLayout());
        itlistPanel.setLayout(new BorderLayout());

        vendaPanel.setLayout(new GridLayout(3,2));
        
        JLabel nomeInputLabel = new JLabel("Insira o nome do cliente: ");
        JTextField nomeInputField = new JTextField();
        JLabel cpfInputLabel = new JLabel("Insira o CPF do cliente: ");
        JTextField cpfInputField = new JTextField();
        
        JLabel totalDinLabel = new JLabel("Total: R$0,00");
        
        
        JLabel catcomboLabel = new JLabel("Selecionar categoria: ");
        JLabel infoLabel = new JLabel("<html>Nome:<br>Preço:<br>Marca:<br>Qtd em Estoque:</html>");
        String[] categorias = {"Bebidas", "Salgados", "Doces"};
        JComboBox<String> categoriaComboBox = new JComboBox<>(categorias);
        
        vendaSelListModel = new DefaultListModel<>();
        vendaSelProdListModel = new DefaultListModel<>();
        JList prodSels = new JList<>(vendaProdutosSel);
        prodSels.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane lsp1 = new JScrollPane(prodSels);
        lsp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        vendaCatListModel = new DefaultListModel<>();
        JList posProds = new JList<>(vendaCatListModel);
        posProds.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane lsp2 = new JScrollPane(posProds);
        lsp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        JButton voltarButton = new JButton("Voltar", backIcon);
        voltarButton.setToolTipText("Voltar");
        voltarButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        voltarButton.setVerticalTextPosition(SwingConstants.CENTER);
        voltarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                voltarTelaInicial();
            }
        });
        
        execVenda.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Venda novaVenda = new Venda();
                for (int i=0;i<vendaSelProdListModel.size();i++) {
                    //System.out.println(vendaSelProdListModel.get(i).getNome());
                    novaVenda.adicionarItem(vendaSelProdListModel.get(i));
                    
                }
                
                novaVenda.setNome(nomeInputField.getText());
                novaVenda.setCpf(cpfInputField.getText());
                
                
                novaVenda.executarVenda();
                vendas.add(novaVenda);
                salvarVendas();
                salvarEstoque();
                voltarTelaInicial();
            }
        }) ;
        
        
        //catlistPanel.add(lsp2, BorderLayout.CENTER);
        //catlistPanel.add(posProds, BorderLayout.SOUTH);
        
                //itlistPanel.add(lsp1);

        //itlistPanel.add(prodSels);
        
        
        
        //vendaPanel.add(catcomboLabel);
        //vendaPanel.add(categoriaComboBox);
        selCatPanel.add(nomeInputLabel);
        selCatPanel.add(nomeInputField);
        selCatPanel.add(cpfInputLabel);
        selCatPanel.add(cpfInputField);
        
        selCatPanel.add(catcomboLabel);
        selCatPanel.add(categoriaComboBox);
        
        //selCatPanel.add(infoLabel);
                selCatPanel.add(totalDinLabel);

        
        //vendaPanel.add(prodSels);
        //vendaPanel.add(posProds);
        //vendaPanel.add(itlistPanel);
        //vendaPanel.add(catlistPanel);
        //vendaPanel.add(listaPanel);
        vendaPanel.add(selCatPanel);
        vendaPanel.add(lsp2);
        vendaPanel.add(infoLabel);
        vendaPanel.add(lsp1);
        vendaPanel.add(voltarButton);  
        vendaPanel.add(execVenda);

        
        vendaCatListModel.clear();
        carregarCatLista(vendaCatListModel,categoriaComboBox.getSelectedItem().toString());
        /*for (Produto produto : estoque.getProdutos()) {
            if (produto.getCategoria().equals(categoriaComboBox.getSelectedItem())) {
                vendaCatListModel.addElement(produto.getNome());
                
            }
        }*/
        
        categoriaComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                vendaCatListModel.clear();
                carregarCatLista(vendaCatListModel,categoriaComboBox.getSelectedItem().toString());
            }
        });
        
        //2 CLICKS = ADD
        posProds.addMouseListener(new MouseAdapter() {
           public void mouseClicked(MouseEvent e) {
               String sel = posProds.getModel().getElementAt(posProds.locationToIndex(e.getPoint())).toString();
               Produto buffer = estoque.existe(sel);
               //vendaProdutosSel.addElement(buffer);
               if (buffer!=null) {
                   if (buffer.getCategoria().equals("Bebidas")) {
                       infoLabel.setText(String.format("<html>Nome: %s<br>Preço: R$%.2f<br>Marca: %s<br>Qtd em Estoque: %d</html>",sel,buffer.getPreco(),buffer.getMarca(),buffer.getQuantidadeEstoque()));
                   } else {
                       infoLabel.setText(String.format("<html>Nome: %s<br>Preço: R$%.2f<br>Produto caseiro</html>",sel,buffer.getPreco(),buffer.getMarca(),buffer.getQuantidadeEstoque()));
                   }
                 
               }
               if (e.getClickCount()%2 == 0 && e.getButton() == MouseEvent.BUTTON1) {
                   int ocor=0;
                   for (int i=0;i<vendaSelListModel.size();i++) {
                       if (vendaSelListModel.get(i).equals(sel)) {
                           ocor++;
                       }
                   }
                   Produto getProdBuffer = estoque.existe(sel);
                   if (getProdBuffer!=null) {
                       if (ocor+1>getProdBuffer.getQuantidadeEstoque()) {
                           JOptionPane.showMessageDialog(null, String.format("No estoque só está disponível %d unidades de %s", getProdBuffer.getQuantidadeEstoque(),sel) ,"ERRO!", JOptionPane.ERROR_MESSAGE);
                       } else {
                           vendaSelListModel.addElement(sel);
                           vendaSelProdListModel.addElement(estoque.existe(sel));

                           float vendaTotalPreco = 0;
                            for (int i=0;i<vendaSelProdListModel.size();i++) {
                                vendaTotalPreco+=vendaSelProdListModel.get(i).getPreco();
                            }
                            totalDinLabel.setText(String.format("R$%.2f",vendaTotalPreco));
                       }
                   }
                   
               }
               
               int occurances=0;
               DefaultListModel<String> listed = new DefaultListModel();
               String target;
               
               vendaProdutosSel.clear();
               for (int i=0;i<vendaSelListModel.size();i++) {
                   if (listed.indexOf(vendaSelListModel.get(i))==-1) {
                       occurances=0;
                       target=vendaSelListModel.get(i);
                       for (int j=0;j<vendaSelListModel.size();j++) {
                           if (vendaSelListModel.get(j).equals(target)) {
                               occurances++;
                           }
                       }
                       vendaProdutosSel.addElement((occurances==1) ? (target) : (String.format("%s %dx",target,occurances)));
                       listed.addElement(target);
                   }
               }
               //System.out.println(vendaProdutosSel);
               
               /*for (int i=0;i<vendaProdutosSel.size();i++) {
                   
               }*/
           }
        });
        
       //2 CLICKS = REMOVE
        prodSels.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                //try {
                String sel = prodSels.getModel().getElementAt(prodSels.locationToIndex(e.getPoint())).toString();
                Produto buffer = estoque.existe(sel);
                if (buffer!=null) {
                    if (buffer.getCategoria().equals("Bebidas")) {
                        infoLabel.setText(String.format("<html>Nome: %s<br>Preço: R$%.2f<br>Marca: %s<br>Qtd em Estoque: %d</html>",sel,buffer.getPreco(),buffer.getMarca(),buffer.getQuantidadeEstoque()));
                    } else {
                        infoLabel.setText(String.format("<html>Nome: %s<br>Preço: R$%.2f<br>Produto caseiro</html>",sel,buffer.getPreco(),buffer.getMarca(),buffer.getQuantidadeEstoque()));
                    }

                }
                if (e.getClickCount()%2 == 0 && e.getButton() == MouseEvent.BUTTON1) {
                    String selBuff = new StringBuilder(sel).reverse().toString();
                    int spacLoc = (selBuff.indexOf(' ')!=-1) ? selBuff.indexOf(' ') : 0;
                    
                    String selBuff2 = (spacLoc!=0) ? sel.substring(0,sel.length()-spacLoc-1) : sel;
                    String amBuffStr="";
                    try {
                    amBuffStr = sel.substring(sel.length()-spacLoc,sel.length()-1);
                    } catch (StringIndexOutOfBoundsException amBuffStrE) { }
                    int amBuff = -1;
                    try {amBuff = Integer.valueOf(amBuffStr); } catch (NumberFormatException excIntString) { }
                    if (spacLoc!=-1 && amBuff !=-1) {
                        
                        
                        int spacLoc2 = vendaSelListModel.indexOf(selBuff2);
                        if (spacLoc2!=-1) {
                            vendaSelListModel.remove(spacLoc2);
                            for (int i=0;i<vendaSelProdListModel.size();i++) {
                                if (vendaSelProdListModel.get(i).getNome().equals(selBuff2)) {
                                    //System.out.println("REMOVED");
                                    vendaSelProdListModel.remove(i);
                                    break;
                                }
                            }
                            
                            int occurances=0;
                            DefaultListModel<String> listed = new DefaultListModel();
                            String target;
                            
                            vendaProdutosSel.clear();
                            for (int i=0;i<vendaSelListModel.size();i++) {
                                if (listed.indexOf(vendaSelListModel.get(i))==-1) {
                                    occurances=0;
                                    target=vendaSelListModel.get(i);
                                    for (int j=0;j<vendaSelListModel.size();j++) {
                                        if (vendaSelListModel.get(j).equals(target)) {
                                            occurances++;
                                        }
                                    }
                                    vendaProdutosSel.addElement((occurances==1) ? (target) : (String.format("%s %dx",target,occurances)));
                                    listed.addElement(target);
                                }
                            }
                            
                            
                        }
                    } else {
                        int spacLoc3 = vendaSelListModel.indexOf(sel);
                        if (spacLoc3!=-1) {
                            vendaSelListModel.remove(spacLoc3);
                            for (int i=0;i<vendaSelProdListModel.size();i++) {
                                    if (vendaSelProdListModel.get(i).getNome()==sel) {
                                        vendaSelProdListModel.remove(i);
                                        break;
                                    }
                                }

                            int occurances=0;
                                DefaultListModel<String> listed = new DefaultListModel();
                                String target;

                                vendaProdutosSel.clear();
                                for (int i=0;i<vendaSelListModel.size();i++) {
                                    if (listed.indexOf(vendaSelListModel.get(i))==-1) {
                                        occurances=0;
                                        target=vendaSelListModel.get(i);
                                        for (int j=0;j<vendaSelListModel.size();j++) {
                                            if (vendaSelListModel.get(j).equals(target)) {
                                                occurances++;
                                            }
                                        }
                                        vendaProdutosSel.addElement((occurances==1) ? (target) : (String.format("%s %dx",target,occurances)));
                                        listed.addElement(target);
                                    }
                                }
                        }
                    }
                    float vendaTotalPreco = 0;
                    for (int i=0;i<vendaSelProdListModel.size();i++) {
                        vendaTotalPreco+=vendaSelProdListModel.get(i).getPreco();
                    }
                    totalDinLabel.setText(String.format("R$%.2f",vendaTotalPreco));
                }
            //} catch (ArrayIndexOutOfBoundsException arrIndOOB) { }
            } 
        });
        
        mainPanel.removeAll();
        //mainPanel.add(itlistPanel, BorderLayout.NORTH);
        mainPanel.add(catlistPanel, BorderLayout.CENTER);
        mainPanel.add(vendaPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    //botão "Finalizar Venda", "Vendas"
    /*
    Antigo; metodo usado agora é executarVenda()
    private void finalizarVenda() {
        JOptionPane.showMessageDialog(null, "Venda finalizada. Total: R$ " + venda.calcularTotal());
        venda.finalizarVenda();
        atualizarValorCarrinho();
    }*/
    
  /*  private void datasVendasfun() {
        JPanel adicionarPanel = new JPanel();
        adicionarPanel.setLayout(new GridLayout(1,2));

        JLabel dataLabel = new JLabel("<html>Data:<br/>(Padrão é a data atual, formato: DD-MM-AAAA)</html>", SwingConstants.CENTER);
        JTextField dataField = new JTextField();
        
        
        JButton voltarButton = new JButton("Voltar");
        voltarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                voltarTelaInicial();
            }
        });
        
        voltarButton = new JButton("Voltar");
        voltarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                voltarTelaInicial();
            }
        });
        
        adicionarPanel.add(dataLabel);
        adicionarPanel.add(dataField);
        adicionarPanel.add(voltarButton);
        
        mainPanel.removeAll();
        mainPanel.add(adicionarPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
        /*JLabel precoLabel = new JLabel("Preço:");
        JTextField precoField = new JTextField();

        JLabel quantidadeLabel = new JLabel("Quantidade:");
        JTextField quantidadeField = new JTextField();*/
//    }

    
    private Produto encontrarProdutoPorNome(String nome) {
        for (Produto produto : estoque.getProdutos()) {
            if (produto.getNome().equals(nome)) {
                return produto;
            }
        }
        return null;
    }

    private ArrayList<Venda> carregarVendas() {
        ArrayList<Venda> buffVendas = new ArrayList<>();
        try (ObjectInputStream vois = new ObjectInputStream(new FileInputStream(VENDA_FILE))) {
            List<Venda> buffVendaFile = (List<Venda>) vois.readObject();
            for (Venda vnd : buffVendaFile) {
                buffVendas.add(vnd);
               // buffVendas.
            }
        } catch (IOException | ClassNotFoundException e) { }
        return buffVendas;
    }
    
    private void salvarVendas() {
        try (ObjectOutputStream voos = new ObjectOutputStream(new FileOutputStream(VENDA_FILE))) {
            //voos.writeObject(new ArrayList<>(vendas));
            ArrayList<Venda> vendaListBuff = new ArrayList<>();
            for (Venda vnd : vendas) {
                vendaListBuff.add(vnd);
               // buffVendas.
            }
            voos.writeObject(new ArrayList<>(vendaListBuff));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void salvarEstoque() {
        //salvar em arquivo
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ESTOQUE_FILE))) {
            oos.writeObject(new ArrayList<>(estoque.getProdutos()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //salvar em sql
        for (Produto prod : estoque.getProdutos()) {
            String checkQuery = "SELECT * FROM produtos WHERE nome='%s';";
            String updateQuery = "INSERT INTO produtos(nome, categoria, marca, preco, quant_estoque) VALUES('%s','%s','%s',%s, %s);";
            String setUpdateQuery = "UPDATE produtos SET categoria='%s', marca='%s', preco='%s', quant_estoque='%s' WHERE nome='%s'";
            
            int resQ = database.ExecuteQuery(String.format(checkQuery,prod.getNome()));
            System.out.println(String.format(checkQuery,prod.getNome()));
            System.out.println(resQ);
            if (resQ==-1) {
                int resU;
                if (prod.getCategoria()!="Bebidas") {
                    resU = database.ExecuteUpdate(String.format(updateQuery,prod.getNome(),prod.getCategoria(),prod.getMarca(),String.valueOf(prod.getPreco()),String.valueOf(prod.getQuantidadeEstoque())));
                    System.out.println(String.format(updateQuery,prod.getNome(),prod.getCategoria(),prod.getMarca(),String.valueOf(prod.getPreco()),String.valueOf(prod.getQuantidadeEstoque())));
                } else {
                    resU = database.ExecuteUpdate(String.format(updateQuery,prod.getNome(),prod.getCategoria(),"Produto Caseiro",String.valueOf(prod.getPreco()),"NULL"));
                    System.out.println(String.format(updateQuery,prod.getNome(),prod.getCategoria(),"Produto Caseiro",String.valueOf(prod.getPreco()),"NULL"));
                }
                //System.out.println(prod.getNome()+" escrita na database = "+resU);
            } else {
                int resU;
                if (prod.getCategoria()!="Bebidas") {
                    resU = database.ExecuteUpdate(String.format(setUpdateQuery,prod.getCategoria(),prod.getMarca(),String.valueOf(prod.getPreco()),String.valueOf(prod.getQuantidadeEstoque()),prod.getNome()));
                    System.out.println(String.format(setUpdateQuery,prod.getCategoria(),prod.getMarca(),String.valueOf(prod.getPreco()),String.valueOf(prod.getQuantidadeEstoque()),prod.getNome()));
                } else {
                    resU = database.ExecuteUpdate(String.format(setUpdateQuery,prod.getCategoria(),"Produto Caseiro",String.valueOf(prod.getPreco()),String.valueOf(prod.getQuantidadeEstoque()),prod.getNome()));
                    System.out.println(String.format(setUpdateQuery,prod.getCategoria(),"Produto Caseiro",String.valueOf(prod.getPreco()),String.valueOf(prod.getQuantidadeEstoque()),prod.getNome()));
                }
            }
        }
        System.out.println("Terminou de salvar no banco de dados.");
        
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
        //Date agora = new Date();
        //System.out.println(new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(agora));
        
        //Date nData = new Date(2013-1900,Calendar.FEBRUARY,20,10,50,25);
        
        //System.out.println(new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(nData));
        System.out.println(database.OpenDatabase());
        
        String coxinhaAddQuery = "INSERT INTO produtos(nome, categoria, marca, preco) VALUES('Coxinha','Salgado','Produto caseiro', 1.5);";
        String coxinhaSelQuery = "SELECT * FROM produtos WHERE categoria='Salgado'";
        
        
        //database.ExecuteQuery(coxinhaSelQuery);
        
        new ProjetoCaixaGUI();
    }
}