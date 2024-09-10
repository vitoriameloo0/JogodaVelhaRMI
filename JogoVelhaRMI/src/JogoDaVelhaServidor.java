import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class JogoDaVelhaServidor extends UnicastRemoteObject implements JogoDaVelhaInterface {
    private List<JogoDaVelhaClienteInterface> clientes = new ArrayList<>();
    private String[] tabuleiro = new String[9];
    private int pontosX = 0;
    private int pontosO = 0;
    private boolean turnoX = true; // X começa o jogo

    protected JogoDaVelhaServidor() throws RemoteException {
        super();
        limparTabuleiro();
    }

    private void limparTabuleiro() {
        for (int i = 0; i < 9; i++) {
            tabuleiro[i] = "";
        }
    }

    @Override
    public synchronized void registrarJogador(JogoDaVelhaClienteInterface cliente, String simbolo)
            throws RemoteException {
        if (clientes.size() <= 2) {
            clientes.add(cliente);
            System.out.println("Jogador registrado: " + simbolo);
            if (clientes.size() == 2) {
                notificarVez();
            }
        } else {
            cliente.atualizarPlacar(pontosX, pontosO); // Atualizar placar caso o jogador entre após o início do jogo
        }
    }

    @Override
    public synchronized void fazerJogada(int index, String simbolo) throws RemoteException {
        System.out.println("Recebendo jogada: " + simbolo + " no índice " + index);

        if (tabuleiro[index].equals("")) {
            tabuleiro[index] = simbolo;
            System.out.println("Jogada registrada no tabuleiro");

            for (JogoDaVelhaClienteInterface cliente : clientes) {
                cliente.atualizarTabuleiro(index, simbolo);
            }

            if (verificarVitoria(simbolo)) {
                for (JogoDaVelhaClienteInterface cliente : clientes) {
                    cliente.notificarVitoria(simbolo);
                }

                if (simbolo.equals("X")) {
                    pontosX++;
                } else {
                    pontosO++;
                }

                reiniciarJogo();
            } else if (verificarEmpate()) {
                for (JogoDaVelhaClienteInterface cliente : clientes) {
                    cliente.notificarVitoria("Empate");
                }
                reiniciarJogo();
            } else {
                turnoX = !turnoX;
                notificarVez();
            }
        } else {
            System.out.println("Jogada inválida, posição já ocupada.");
        }
    }

    @Override
    public void reiniciarJogo() throws RemoteException {
        limparTabuleiro();
        turnoX = true;

        for (JogoDaVelhaClienteInterface cliente : clientes) {
            cliente.atualizarPlacar(pontosX, pontosO);
        }

        notificarVez();
    }

    @Override
    public void zerarPlacar() throws RemoteException {
        pontosX = 0;
        pontosO = 0;
        reiniciarJogo();
    }

    private void notificarVez() throws RemoteException {
        if (turnoX) {
            System.out.println("É a vez do jogador X");
            clientes.get(0).notificarSuaVez();
        } else {
            System.out.println("É a vez do jogador O");
            clientes.get(1).notificarSuaVez();
        }
    }

    private boolean verificarVitoria(String simbolo) {
        int[][] combinacoes = {
                { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 },
                { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 },
                { 0, 4, 8 }, { 2, 4, 6 }
        };

        for (int[] combinacao : combinacoes) {
            if (tabuleiro[combinacao[0]].equals(simbolo) &&
                    tabuleiro[combinacao[1]].equals(simbolo) &&
                    tabuleiro[combinacao[2]].equals(simbolo)) {
                return true;
            }
        }

        return false;
    }

    private boolean verificarEmpate() {
        for (String s : tabuleiro) {
            if (s.equals("")) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(2000);
            JogoDaVelhaServidor servidor = new JogoDaVelhaServidor();
            java.rmi.Naming.rebind("rmi://localhost:2000/JogoDaVelha", servidor);
            System.out.println("Servidor RMI pronto.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
