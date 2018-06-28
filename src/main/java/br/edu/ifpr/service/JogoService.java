package br.edu.ifpr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifpr.domain.Contato;
import br.edu.ifpr.domain.Jogada;
import br.edu.ifpr.domain.Jogo;
import br.edu.ifpr.repository.JogoRepository;
import br.edu.ifpr.repository.UsuarioRepository;
import br.edu.ifpr.websocket.UsuariosConectados;
import br.edu.ifpr.websocket.WebSocketService;

@Service
public class JogoService {

	private static final Logger log = LoggerFactory.getLogger(JogoService.class);
	
	private final UsuarioRepository usuarioRepository;
	private final JogoRepository jogoRepository;
	private final WebSocketService webSocketService;
	private final UsuariosConectados usuariosConectados;
	
	private ContatoService contatoService;
	
	public JogoService(UsuarioRepository usuarioRepository, JogoRepository jogoRepository,
			WebSocketService webSocketService, UsuariosConectados usuariosConectados) {
		this.usuarioRepository = usuarioRepository;
		this.jogoRepository = jogoRepository;
		this.webSocketService = webSocketService;
		this.usuariosConectados = usuariosConectados;
	}

	@Autowired
	public void setContatoService(ContatoService contatoService) {
		this.contatoService = contatoService;
	}

	
	public void Jogada(Jogo jogo, String emissor, Integer rodada, Jogada jogada) {
		
		Contato contatoEmissor = contatoService.recuperaContato(emissor, jogo.getDestinatario().getLogin());
		
		jogo.setJogada(jogada);
		jogo.setRodada(rodada);
		jogo.setDestinatario(contatoEmissor.getContato());

		
		Contato contatoDestinatario = contatoService.recuperaContato(jogo.getDestinatario().getLogin(), emissor);

	}

}
