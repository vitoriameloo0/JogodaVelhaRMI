import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JogoDaVelhaInterface extends Remote {
    void novaJogada(int index, String player) throws RemoteException;

    void novoJogo() throws RemoteException;

    void zerarPlacar() throws RemoteException;

    String obterPlacar() throws RemoteException;

    String obterTabuleiro() throws RemoteException;

    void registrarCliente(JogoDaVelhaClienteInterface cliente) throws RemoteException;
}
