import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class JogoDaVelhaServidor extends UnicastRemoteObject implements JogoDaVelhaInterface {
    private String[] tabuleiro = new String[9];
    private int PX = 0;
    private int PO = 0;
    private List<JogoDaVelhaClienteInterface> clientes = new ArrayList<>();
    private boolean turnoX = true;

    protected JogoDaVelhaServidor() throws RemoteException {
        super();
        for (int i = 0; i < 9; i++) {
            tabuleiro[i] = "";
        }
    }

    @Override
    public synchronized void novaJogada(int index, String player) throws RemoteException {
        if (tabuleiro[index].equals("")) {
            tabuleiro[index] = player;
            System.out.println("Jogada recebida: " + player + " no índice " + index);
            notificarClientes();
            verificarVitoria();
            alternarTurno();
        } else {
            System.out.println("Jogada inválida! Posição já ocupada."); // Jogada inválida
        }
    }

    @Override
    public void novoJogo() throws RemoteException {
        for (int i = 0; i < 9; i++) {
            tabuleiro[i] = "";
        }
        turnoX = true;
        notificarClientes();
    }

    @Override
    public void zerarPlacar() throws RemoteException {
        PX = 0;
        PO = 0;
        notificarClientes();
    }

    @Override
    public String obterPlacar() throws RemoteException {
        return "X -> " + PX + " | O -> " + PO;
    }

    @Override
    public String obterTabuleiro() throws RemoteException {
        StringBuilder estado = new StringBuilder();
        for (int i = 0; i < tabuleiro.length; i++) {
            estado.append(tabuleiro[i].isEmpty() ? "" : tabuleiro[i]);
            if (i < tabuleiro.length - 1) {
                estado.append(",");
            }
        }
        return estado.toString();
    }

    @Override
    public void registrarCliente(JogoDaVelhaClienteInterface cliente) throws RemoteException {
        clientes.add(cliente);
        System.out.println("Cliente registrado: " + cliente);
        cliente.atualizarTabuleiro(obterTabuleiro());
        if (turnoX) {
            cliente.suaVez("X");
        }
    }

    private void alternarTurno() throws RemoteException {
        turnoX = !turnoX;
        for (JogoDaVelhaClienteInterface cliente : clientes) {
            cliente.suaVez(turnoX ? "X" : "O");
            System.out.println("Notificando cliente: Sua vez: " + (turnoX ? "X" : "O"));
        }
    }

    private void notificarClientes() throws RemoteException {
        String estadoTabuleiro = obterTabuleiro();
        for (JogoDaVelhaClienteInterface cliente : clientes) {
            cliente.atualizarTabuleiro(estadoTabuleiro);
        }
    }

    private void verificarVitoria() throws RemoteException {
        String[][] combinacoesVitoria = {
                { tabuleiro[0], tabuleiro[1], tabuleiro[2] },
                { tabuleiro[3], tabuleiro[4], tabuleiro[5] },
                { tabuleiro[6], tabuleiro[7], tabuleiro[8] },
                { tabuleiro[0], tabuleiro[3], tabuleiro[6] },
                { tabuleiro[1], tabuleiro[4], tabuleiro[7] },
                { tabuleiro[2], tabuleiro[5], tabuleiro[8] },
                { tabuleiro[0], tabuleiro[4], tabuleiro[8] },
                { tabuleiro[2], tabuleiro[4], tabuleiro[6] }
        };

        // Verificar se X ganhou
        for (String[] combinacao : combinacoesVitoria) {
            if (combinacao[0].equals("X") && combinacao[1].equals("X") && combinacao[2].equals("X")) {
                // Jogador X venceu
                PX++;
                notificarClientes(); // Atualiza o placar e o tabuleiro para ambos os clientes
                novoJogo(); // Inicia um novo jogo automaticamente
                return;
            }
        }

        // Verificar se O ganhou
        for (String[] combinacao : combinacoesVitoria) {
            if (combinacao[0].equals("O") && combinacao[1].equals("O") && combinacao[2].equals("O")) {
                // Jogador O venceu
                PO++;
                notificarClientes(); // Atualiza o placar e o tabuleiro para ambos os clientes
                novoJogo(); // Inicia um novo jogo automaticamente
                return;
            }
        }

        // Verificar se há empate (todos os campos preenchidos e sem vitória)
        boolean empate = true;
        for (String valor : tabuleiro) {
            if (valor.equals("")) {
                empate = false;
                break;
            }
        }

        if (empate) {
            // Se todos os campos estiverem preenchidos e ninguém ganhou, é um empate
            notificarClientes(); // Atualiza o tabuleiro para ambos os clientes
            novoJogo(); // Inicia um novo jogo automaticamente
        }
    }

    public static void main(String[] args) {
        try {
            JogoDaVelhaServidor servidor = new JogoDaVelhaServidor();
            java.rmi.Naming.rebind("//localhost/JogoDaVelha", servidor);
            System.out.println("Servidor RMI está pronto.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
