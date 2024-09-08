import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JogoDaVelhaServer extends Remote {
    void registrarCliente(JogoDaVelhaCliente cliente) throws RemoteException;
    void enviarJogada(int index, String jogadorSimbolo) throws RemoteException;
    void novoJogo() throws RemoteException;
    void zerarPlacar() throws RemoteException;
    String getPlacar() throws RemoteException;
}