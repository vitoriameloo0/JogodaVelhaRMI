import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class JogoDaVelhaCliente {
    private static JogoDaVelhaClienteInterface jogador;
    private static TelaJogador tela;
    private static String  ip = "172.16.65.192";
    private static String nomeServico = "JogoDaVelha";
    
    public static void main(String args[]) {
    	
    	if(args.length > 0) {
    		try {
    			ip = args[0];
    		}catch(Exception e) {
    			System.out.println("Ip invalido");
    		}
    	}

    	/*Tenta localizar o serviço a partir do protocolo rmi e informações do servidor*/
        try {
            String url = "rmi://" + ip + "/" + nomeServico;
            JogoDaVelhaServidorInterface server = (JogoDaVelhaServidorInterface) Naming.lookup(url);
            /*Cria um jogador e uma tela, define a qual servidor essa tela pertence e então atribui
             * a mesma ao cliente(jogador)*/
            jogador = new JogoDaVelhaClienteImplementacao();
            tela = new TelaJogador();
            tela.setServidor(server);
            jogador.setTelaJogador(tela);
            jogador.setServidor(server);
            /*Loga o cliente no servidor*/
            server.logar(jogador);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaJogador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaJogador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaJogador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaJogador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

       
    }
}
