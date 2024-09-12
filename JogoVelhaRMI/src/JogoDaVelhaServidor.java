import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class JogoDaVelhaServidor {

    private static String ip = " ";
    private static String nomeServico = "JogoDaVelha";

    public static void main(String args[]) {
        JogoDaVelhaServidorInterface server;

        /*Tenta pegar o registro RMI a partir da porta que o servidor escutara. Após isto,
         * o servidor é inicializado e então é feito um bind entre o serviço e o servidor*/
        try {
            //Registry registry = LocateRegistry.getRegistry(Integer.parseInt(porta));
            //registry.list();
            String url = "rmi://" + ip + "/" + nomeServico;
            server = new JogoDaVelhaServidorIMP();
            Naming.rebind(url, server);
            //System.setProperty("java.rmi.server.hostname",ip);
        } catch (RemoteException e) {
            System.out.println("Remote Exception: " + e.getMessage());
        } catch (MalformedURLException e) {
            System.out.println("URL mal formada: " + e.getMessage());
        }

    }

    
}
