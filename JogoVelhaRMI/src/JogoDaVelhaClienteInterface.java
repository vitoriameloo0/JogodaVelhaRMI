import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JogoDaVelhaClienteInterface extends Remote {
    void atualizarTabuleiro(int index, String simbolo) throws RemoteException;

    void atualizarPlacar(int pontosX, int pontosO) throws RemoteException;

    void notificarVitoria(String vencedor) throws RemoteException;

    void notificarSuaVez() throws RemoteException;
}
