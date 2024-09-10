import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JogoDaVelhaInterface extends Remote {
    void registrarJogador(JogoDaVelhaClienteInterface cliente, String simbolo) throws RemoteException;

    void fazerJogada(int index, String simbolo) throws RemoteException;

    void reiniciarJogo() throws RemoteException;

    void zerarPlacar() throws RemoteException;
}
