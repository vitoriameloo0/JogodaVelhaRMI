import java.rmi.Naming;
import java.rmi.RemoteException;
import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;

public class JogoDaVelhaCliente extends JFrame implements JogoDaVelhaClienteInterface {
    private JButton[] bt = new JButton[9];
    private JButton novo = new JButton("Novo Jogo");
    private JButton zerar = new JButton("Zerar Placar");

    private JLabel placar = new JLabel("PLACAR");
    private JLabel px = new JLabel("X -> 0");
    private JLabel po = new JLabel("O -> 0");

    private boolean minhaVez = false;
    private String jogadorSimbolo;

    private JogoDaVelhaServidorInterface servidor;

    public JogoDaVelhaCliente(String jogadorSimbolo) throws RemoteException {
        this.jogadorSimbolo = jogadorSimbolo;

        try {
            servidor = (JogoDaVelhaServidorInterface) Naming.lookup("//localhost:2000/JogoDaVelha");
            servidor.registrarJogador(this, jogadorSimbolo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Definir a tela
        setVisible(true);
        setTitle("Jogo da Velha");
        setDefaultCloseOperation(3);
        setLayout(null);
        setBounds(500, 300, 700, 500);

        add(placar);
        add(px);
        add(po);
        placar.setBounds(450, 50, 100, 30);
        px.setBounds(430, 75, 140, 50);
        po.setBounds(480, 75, 140, 50);

        add(novo);
        add(zerar);
        novo.setBounds(410, 130, 140, 30);
        zerar.setBounds(410, 180, 140, 30);

        novo.addActionListener((ActionEvent e) -> {
            try {
                servidor.reiniciarJogo();
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
        });

        zerar.addActionListener((ActionEvent e) -> {
            try {
                servidor.zerarPlacar();
            } catch (RemoteException remoteException) {
                remoteException.printStackTrace();
            }
        });

        criarBotao(bt);

        if (jogadorSimbolo == "X") {
            minhaVez = true;
        }

        for (int i = 0; i < 9; i++) {
            int index = i;
            bt[i].addActionListener((ActionEvent e) -> {
                if (minhaVez && bt[index].getText().equals("")) {
                    System.out.println("Cliente fazendo jogada no índice " + index);
                    try {
                        servidor.fazerJogada(index, jogadorSimbolo);
                        minhaVez = false;
                    } catch (RemoteException remoteException) {
                        remoteException.printStackTrace();
                    }
                } else {
                    System.out.println("Jogada invalida , oiii");
                }
            });
        }
    }

    private void criarBotao(JButton[] bt) {
        int cont = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                bt[cont] = new JButton();
                add(bt[cont]);
                bt[cont].setBounds((100 * j) + 50, (100 * i) + 50, 95, 95);
                bt[cont].setFont(new Font("Arial", Font.BOLD, 40));
                cont++;
            }
        }
    }

    @Override
    public void atualizarTabuleiro(int index, String simbolo) throws RemoteException {
        System.out.println("Atualizando tabuleiro no cliente. Índice: " + index + " , Símbolo: " + simbolo);
        bt[index].setText(simbolo);
    }

    @Override
    public void atualizarPlacar(int pontosX, int pontosO) throws RemoteException {
        px.setText("X -> " + pontosX);
        po.setText("O -> " + pontosO);
    }

    @Override
    public void notificarVitoria(String vencedor) throws RemoteException {
        JOptionPane.showMessageDialog(this, "Jogador " + vencedor + " venceu!");
    }

    @Override
    public void notificarSuaVez() throws RemoteException {
        System.out.println("É a sua vez! Jogador: " + jogadorSimbolo);
        minhaVez = true;
        JOptionPane.showMessageDialog(this, "Sua vez!");
    }

    public static void main(String[] args) {
        try {
            new JogoDaVelhaCliente(args[0]); // Passar "X" ou "O" como argumento ao rodar o cliente
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
