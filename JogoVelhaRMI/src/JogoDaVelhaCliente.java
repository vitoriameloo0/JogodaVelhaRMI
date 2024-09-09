import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class JogoDaVelhaCliente extends JFrame implements JogoDaVelhaClienteInterface {
    JButton[] bt = new JButton[9];
    JLabel placar = new JLabel("PLACAR");
    JLabel px = new JLabel("X -> 0");
    JLabel po = new JLabel("O -> 0");
    boolean minhaVez = false;
    String jogadorSimbolo;

    JogoDaVelhaInterface servidor;

    public JogoDaVelhaCliente(String jogadorSimbolo) {
        this.jogadorSimbolo = jogadorSimbolo;
        try {
            servidor = (JogoDaVelhaInterface) Naming.lookup("//localhost/JogoDaVelha");
            servidor.registrarCliente(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setVisible(true);
        setTitle("Jogo da Velha");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setBounds(500, 300, 700, 500);

        add(placar);
        add(px);
        add(po);
        placar.setBounds(450, 50, 100, 30);
        px.setBounds(430, 75, 140, 50);
        po.setBounds(480, 75, 140, 50);

        criarBotoes();

    }

    public void criarBotoes() {
        bt = new JButton[9];
        for (int i = 0; i < 9; i++) {
            int index = i;
            bt[i] = new JButton();
            bt[i].setBounds((100 * (i % 3)) + 50, (100 * (i / 3)) + 50, 95, 95);
            bt[i].setFont(new Font("Arial", Font.BOLD, 40));
            add(bt[i]);

            bt[i].addActionListener(e -> {
                System.out.println("Botão " + index + " clicado");
                if (minhaVez && bt[index].getText().equals("")) {
                    try {
                        System.out.println("Jogada enviada: " + jogadorSimbolo + " no índice " + index);
                        servidor.novaJogada(index, jogadorSimbolo);
                        minhaVez = false;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("Não é sua vez ou a posição já está preenchida.");
                }
            });
        }
    }

    @Override
    public void suaVez(String jogadorSimbolo) throws RemoteException {
        minhaVez = this.jogadorSimbolo.equals(jogadorSimbolo);
        System.out.println("Sua vez: " + jogadorSimbolo);
    }

    @Override
    public void atualizarTabuleiro(String estadoTabuleiro) throws RemoteException {
        String[] partes = estadoTabuleiro.split(",");
        for (int i = 0; i < partes.length; i++) {
            bt[i].setText(partes[i]);
        }
    }

    public static void main(String[] args) {
        String jogadorSimbolo = args.length > 0 ? args[0] : "X";
        new JogoDaVelhaCliente(jogadorSimbolo);
    }
}
