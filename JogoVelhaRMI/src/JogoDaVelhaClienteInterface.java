import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JogoDaVelhaClienteInterface extends Remote {
    void suaVez(String jogadorSimbolo) throws RemoteException;

    void atualizarTabuleiro(String estadoTabuleiro) throws RemoteException;
}
