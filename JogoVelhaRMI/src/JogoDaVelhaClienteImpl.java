import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class JogoDaVelhaClienteImpl extends JFrame implements JogoDaVelhaCliente {

    private JButton[] bt = new JButton[9];
    private boolean minhaVez = false;
    private JogoDaVelhaServer servidor;
    private String jogadorSimbolo;

    // Labels para o placar
    private JLabel placarX;
    private JLabel placarO;

    public JogoDaVelhaClienteImpl(String jogadorSimbolo) {
        this.jogadorSimbolo = jogadorSimbolo;

        try {
            servidor = (JogoDaVelhaServer) Naming.lookup("//localhost/JogoDaVelha");
            servidor.registrarCliente(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Configuração da interface gráfica
        setTitle("Jogo da Velha - " + jogadorSimbolo);
        setLayout(null);

        // Inicializa os labels de placar
        placarX = new JLabel("X -> 0");
        placarO = new JLabel("O -> 0");

        // Configura a posição dos labels na interface
        placarX.setBounds(350, 50, 100, 30); // Ajuste as posições conforme necessário
        placarO.setBounds(350, 80, 100, 30);

        add(placarX);
        add(placarO);

        // Inicializando os botões para o tabuleiro
        setLayout(new GridLayout(3, 3));
        for (int i = 0; i < 9; i++) {
            bt[i] = new JButton("");
            int index = i;
            bt[i].addActionListener((ActionEvent e) -> {
                if (minhaVez && bt[index].getText().equals("")) {
                    bt[index].setText(jogadorSimbolo);
                    minhaVez = false;
                    try {
                        servidor.enviarJogada(index, jogadorSimbolo);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            add(bt[i]);
        }

        setSize(500, 500);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void receberJogada(int index, String jogadorSimbolo) throws RemoteException {
        bt[index].setText(jogadorSimbolo);
    }

    @Override
    public void notificarVez(String jogadorSimbolo) throws RemoteException {
        minhaVez = this.jogadorSimbolo.equals(jogadorSimbolo);
        JOptionPane.showMessageDialog(null, "Sua vez, " + jogadorSimbolo);
    }

    @Override
    public void atualizarPlacar(String placar) throws RemoteException {
        // O placar vem no formato "X -> X_POINTS, O -> O_POINTS"
        // Exemplo: "X -> 3, O -> 2"
        String[] partes = placar.split(", ");
        String placarXString = partes[0];
        String placarOString = partes[1];

        // Atualiza os labels do placar
        placarX.setText(placarXString);
        placarO.setText(placarOString);
    }

    @Override
    public void notificarVencedor(String vencedor) throws RemoteException {
        JOptionPane.showMessageDialog(null, vencedor + " ganhou!");
    }

    @Override
    public void limparTabuleiro() throws RemoteException {
        for (JButton button : bt) {
            button.setText("");
        }
    }

    public static void main(String[] args) {
        String jogadorSimbolo = args.length > 0 ? args[0] : "X";
        new JogoDaVelhaClienteImpl(jogadorSimbolo);
    }
}
