import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JogoDaVelhaServidorIMP extends UnicastRemoteObject implements JogoDaVelhaServidorInterface {
   private int tabuleiro[][];
    private int numeroJogada;
    private Map<Integer, JogoDaVelhaClienteInterface> jogadores;
    private int idJogador;
    private Placar placar;

    private static final long serialVersionUID = 1L;

    protected JogoDaVelhaServidorIMP() throws RemoteException {
        super();
        placar = new Placar();
        jogadores = new HashMap<Integer, JogoDaVelhaClienteInterface>();
        idJogador = 0;

        tabuleiro = new int[3][3];
        numeroJogada = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tabuleiro[i][j] = 9;
            }
        }
    }

    @Override
    public synchronized void logar(JogoDaVelhaClienteInterface cliente) throws RemoteException {
        if (idJogador < 2) {
            jogadores.put(idJogador, cliente);
            System.out.println("Server: jogador " + idJogador + " logado com sucesso.");

            System.out.println("Server: " + jogadores.size() + " jogadores logados.");
            cliente.setIdJogador(idJogador);
            System.out.println("Server: id do jogador: " + idJogador);
            cliente.getRespostaServidor("Logado com sucesso.");
            idJogador++;
            if (idJogador < 2) {
                cliente.getRespostaServidor("Aguardando o próximo jogador...");
            } else {
                for (int i = 0; i < 2; i++) {
                    jogadores.get(i).getRespostaServidor("Aguarde a jogada do outro jogador!");
                }
                atualizaPlacar();
                iniciarPartida();
            }
        } else {
            cliente.getRespostaServidor("Impossível logar: O servidor está lotado. ");
            cliente.setIdJogador(-1);
//            cliente.finalizarProcessoCliente();
        }

    }
    @Override
    public void jogar(int id, int linha, int coluna) throws RemoteException {
    	
    	/*Jogada especial que contém tanto linha quanto coluna -1
    	 * Está jogada significa deisitencia/tela fechada*/
    	if(jogadores.containsKey(id)) {//Testa se o jogador está jogando
	    	if(linha < 0 || coluna < 0) {
	    		/*Caso tenha mais de um jogador logado, apresentar a mensagem de vencedor para o outro jogador
	    		 * e então finalizar o jogo para o vencedor. O desistente será finalizado
	    		 * 
	    		 * Caso tenha apenas um jogador, este será finalizado*/
	    		if(jogadores.size() > 1) {
		    		jogadores.get(Math.abs(1 - id)).getRespostaServidor("Voce venceu!");
		        	jogadores.get(Math.abs(1 - id)).finalizarJogo();
		        	//Cria um novo jogador para adquirir a interface do id, remover o id do hash e então
		        	//destruir a referencia desta interface
		        	JogoDaVelhaClienteInterface jogador = jogadores.get(id);
		        	jogadores.remove(id);
		        	
		        	
		        	resetaPartida();
		        	
		        	
		        	jogador.finalizarProcessoCliente();		        	
	    		}
	    		else {
//	    			jogadores.get(id).finalizarProcessoCliente();
	    			
	    			JogoDaVelhaClienteInterface jogador = jogadores.get(id);
		        	jogadores.remove(id);
		        	
		        	resetaPartida();
		        	
		        	jogador.finalizarProcessoCliente();

	    		}	
	    	}
    	}
    	else//Caso não esteja, manda o Exception que causa o cliente a deslogar sem afetar o jogo
    		throw new RemoteException();

        this.tabuleiro[linha][coluna] = id;
        numeroJogada++;
        if (numeroJogada >= 5) {
            boolean isVencedor = processarVencedor(id);
            if (isVencedor) {               
                jogadores.get(id).getRespostaServidor("Voce venceu!");
                jogadores.get(id).finalizarJogo();
                jogadores.get(Math.abs(1 - id)).getJogadaAdversario(id, linha, coluna);
                jogadores.get(Math.abs(1 - id)).getRespostaServidor("Voce perdeu!");
                jogadores.get(Math.abs(1 - id)).finalizarJogo();
                placar.setIdVencedor(id);
				resetaPartida();
            } else {
                if (numeroJogada == 9) {
                    jogadores.get(id).getRespostaServidor("Deu Velha!");
                    jogadores.get(Math.abs(1 - id)).getJogadaAdversario(id, linha, coluna);
                    jogadores.get(Math.abs(1 - id)).getRespostaServidor("Deu vellha!");
                    resetaPartida();

                } else {               
                    jogadores.get(id).getRespostaServidor("Aguarde a jogada do outro jogador!");
                    jogadores.get(Math.abs(1 - id)).getJogadaAdversario(id, linha, coluna);
                    jogadores.get(Math.abs(1 - id)).autorizarJogada(this);
                }
            }
        } else {        
            jogadores.get(id).getRespostaServidor("Aguarde a jogada do outro jogador!");
            jogadores.get(Math.abs(1 - id)).getJogadaAdversario(id, linha, coluna);
            jogadores.get(Math.abs(1 - id)).autorizarJogada(this);
        }
    }

    @Override
    public synchronized void deslogar(JogoDaVelhaClienteInterface cliente) throws RemoteException {
        // TODO Auto-generated method stub

    }

    private void sortearPrimeiroJogador() {
        if (Math.random() < 0.5) {
            try {
                jogadores.get(0).autorizarJogada(this);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                jogadores.get(1).autorizarJogada(this);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void iniciarPartida() {
        sortearPrimeiroJogador();
    }
    private void atualizaPlacar() {
    	placar.atualizarPlacar();
    	String pontuacao = placar.toString();
    	try {
			jogadores.get(0).setPlacar(pontuacao);
			jogadores.get(1).setPlacar(pontuacao);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    	
    	
    }
    
    private void resetaPartida() {
    	numeroJogada = 0;
    	
    	for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                tabuleiro[i][j] = 9;
            }
        }
    	
    	if(jogadores.containsKey(0)) {
    		try {
    			jogadores.get(0).resetaTela();
    			jogadores.get(0).criarTela(0);
			} catch (RemoteException e) {}
    		
    	}
    	if(jogadores.containsKey(1)) {
	    	try {
	    		jogadores.get(1).resetaTela();
	    		jogadores.get(1).criarTela(1);
	    	}
	    	catch(Exception e) {}
    	}

    	if(jogadores.size() > 1) {
    		atualizaPlacar();
    		sortearPrimeiroJogador();
    	}
    	else {
    		placar.limpaPlacar();
    		if(jogadores.size() == 0)
    			idJogador = 0;
    		else {
    			idJogador = 1;
	    		if(jogadores.containsKey(1)) {
	    			JogoDaVelhaClienteInterface jogador = jogadores.get(1);
	    			jogadores.remove(1);
	    			try {
						jogador.setIdJogador(0);
						jogadores.put(0, jogador);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
	    		}
    		}	
    	}
    }

    /*Método que processa se jogador é vencedor analisando linhas, colunas e diagonais */
    private boolean processarVencedor(int idJogador) {
        boolean resultado = false;

        // Verifica linhas
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (tabuleiro[i][j] != idJogador) {
                    break;
                }
                if (j == 2) {
                    resultado = true;
                    System.out.println("Linhas Verificadas resultado = " + resultado);
                    return true;
                }
            }
            if (resultado) {
                break;
            }
        }
        System.out.println("Linhas Verificadas resultado = " + resultado);
        // Verifica colunas
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                if (tabuleiro[i][j] != idJogador) {
                    break;
                }
                if (i == 2) {
                    resultado = true;
                    System.out.println("Colunas Verificadas resultado = " + resultado);
                    return true;
                }
            }
            if (resultado) {
                break;
            }
        }
        System.out.println("Colunas Verificadas resultado = " + resultado);
        // Verifica diagonal principal
        for (int i = 0; i < 3; i++) {
            if (tabuleiro[i][i] != idJogador) {
                break;
            }
            if (i == 2) {
                resultado = true;
                System.out.println("Diagonal Principal Verificada resultado = " + resultado);
                return true;
            }
        }
        System.out.println("Diagonal Principal Verificada resultado = " + resultado);

        // Verifica diagonal secundaria
        for (int i = 2, j = 0; i >= 0; i--, j++) {
            if (tabuleiro[i][j] != idJogador) {
                break;
            }
            if (j == 2) {
                resultado = true;
                System.out.println("Diagonal Secundaria Verificada resultado = " + resultado);
                return true;
            }
        }
        System.out.println("Diagonal Secundaria Verificada resultado = " + resultado);
        System.out.println("__________________________________________________");
        return resultado;
    }
}
