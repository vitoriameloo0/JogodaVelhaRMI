import java.rmi.Remote;
import java.rmi.RemoteException;

public interface JogoDaVelhaCliente extends Remote {
    void receberJogada(int index, String jogadorSimbolo) throws RemoteException;
    void notificarVez(String jogadorSimbolo) throws RemoteException;
    void atualizarPlacar(String placar) throws RemoteException;
    void notificarVencedor(String vencedor) throws RemoteException;
    void limparTabuleiro() throws RemoteException;
}