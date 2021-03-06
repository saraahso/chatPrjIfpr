package br.edu.ifpr.service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifpr.domain.Contato;
import br.edu.ifpr.domain.Mensagem;
import br.edu.ifpr.domain.Usuario;
import br.edu.ifpr.repository.MensagemRepository;
import br.edu.ifpr.repository.UsuarioRepository;
import br.edu.ifpr.websocket.UsuariosConectados;
import br.edu.ifpr.websocket.WebSocketService;

@Service
public class MensagemService {

	private static final Logger log = LoggerFactory.getLogger(MensagemService.class);
	
	private final UsuarioRepository usuarioRepository;
	private final MensagemRepository mensagemRepository;
	private final WebSocketService webSocketService;
	private final UsuariosConectados usuariosConectados;
	
	private ContatoService contatoService;
	
	public MensagemService(UsuarioRepository usuarioRepository, MensagemRepository mensagemRepository,
			WebSocketService webSocketService, UsuariosConectados usuariosConectados) {
		this.usuarioRepository = usuarioRepository;
		this.mensagemRepository = mensagemRepository;
		this.webSocketService = webSocketService;
		this.usuariosConectados = usuariosConectados;
	}

	@Autowired
	public void setContatoService(ContatoService contatoService) {
		this.contatoService = contatoService;
	}

	public Iterable<Mensagem> listarMensagensDestinatarioEmissor(String loginDestinatario, String loginEmissor) {
		log.info("Listando mensagens trocadas entre o destinatário {} e o emissor {}...", loginDestinatario, loginEmissor);

		Contato contato = contatoService.recuperaContato(loginEmissor, loginDestinatario);

		Usuario destinatario = usuarioRepository.findByLogin(loginDestinatario);
		Usuario emissor = usuarioRepository.findByLogin(loginEmissor);
		
		Iterable<Mensagem> mensagensEnviadas = mensagemRepository.findAllByDestinatarioAndEmissorOrderByDataEnvio(destinatario, emissor);
		Set<Mensagem> mensagens = new TreeSet<>((Mensagem o1, Mensagem o2) -> {
			return o1.getDataEnvio().compareTo(o2.getDataEnvio());
		});
		
		mensagensEnviadas.forEach(m -> mensagens.add(m));

		if(contato != null && BooleanUtils.isNotTrue(contato.getBloqueado())) {
			Iterable<Mensagem> mensagensRecebidas = mensagemRepository.findAllByDestinatarioAndEmissorOrderByDataEnvio(emissor, destinatario);
			mensagensRecebidas.forEach(m -> mensagens.add(m));
		} else {
			log.info("Contato bloqueado ou não existe. Não vai recuperar as mensagens recebidas");
		}
		
		log.debug("{} mensagens trocadas entre o destinatário {} e o emissor {}.", mensagens.size(), loginDestinatario, loginEmissor);
		
		return mensagens;
	}

	public Integer marcarMensagensLidas(String loginEmissor, String loginDestinatario) {
		log.info("Marcando mensagens como lidas. {} -> {}...", loginEmissor, loginDestinatario);

		Contato contatoDestinatario = contatoService.recuperaContato(loginDestinatario, loginEmissor);
		
		Integer qtd = 0;

		if(contatoDestinatario != null && BooleanUtils.isNotTrue(contatoDestinatario.getBloqueado())) {
			Usuario destinatario = contatoDestinatario.getPrincipal();
			Usuario emissor = contatoDestinatario.getContato();
			
			Iterable<Mensagem> mensagens = mensagemRepository.findAllByDestinatarioAndEmissorOrderByDataEnvio(destinatario, emissor);	
			
			for (Mensagem mensagem : mensagens) {
				if(BooleanUtils.isNotTrue(mensagem.getLida())) {
					mensagem.setLida(true);
					qtd++;
				}
			}
	
			mensagemRepository.save(mensagens);
		} else {
			log.info("Contato bloqueado ou não existe. Não vai marcar mensagens como lidas");
		}
		
		return qtd;
	}
	
	public Integer recuperaQuantidadeMensagensNaoLidas(String loginEmissor, String loginDestinatario) {
		Contato contatoDestinatario = contatoService.recuperaContato(loginDestinatario, loginEmissor);
		Integer qtd = 0;
		if(contatoDestinatario != null && BooleanUtils.isNotTrue(contatoDestinatario.getBloqueado())) {
			qtd = mensagemRepository.recuperaQuantidadeMensagensNaoLidas(loginEmissor, loginDestinatario);
			log.debug("quantidade {} -> {}: {}", loginEmissor, loginDestinatario, qtd);
		} else {
			log.info("Contato bloqueado ou não existe. Não vai recuperar quantidade de mensagens não lidas.");
		}
		return qtd;
	}

	public void enviarMensagem(Mensagem mensagem, String emissor) {
		log.info("Enviando mensagem - destinatario: {}, emissor: {}, conteudo: {}...", mensagem.getDestinatario().getLogin(), emissor, mensagem.getConteudo());
		
		Contato contatoEmissor = contatoService.recuperaContato(emissor, mensagem.getDestinatario().getLogin());
		mensagem
			.setEmissor(contatoEmissor.getPrincipal())
			.setDestinatario(contatoEmissor.getContato())
			.setDataEnvio(LocalDateTime.now())
			.setLida(false);
		mensagemRepository.save(mensagem);
		
		Contato contatoDestinatario = contatoService.recuperaContato(mensagem.getDestinatario().getLogin(), emissor);

		if(usuariosConectados.estaConectado(mensagem.getDestinatario().getLogin())
				&& contatoDestinatario != null && BooleanUtils.isNotTrue(contatoDestinatario.getBloqueado())) {

			webSocketService.enviarMensagem(mensagem);
		} else {
			log.info("Contato bloqueado ou não existe. Não vai enviar mensagem pro websocket.");
		}
	}

	public void marcarMensagemLida(Long id) {
		Mensagem lida = mensagemRepository.findOne(id).setLida(true);
		mensagemRepository.save(lida);
	}
}
