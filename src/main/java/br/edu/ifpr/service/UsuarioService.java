package br.edu.ifpr.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.edu.ifpr.domain.Contato;
import br.edu.ifpr.domain.UserRole;
import br.edu.ifpr.domain.Usuario;
import br.edu.ifpr.repository.ContatoRepository;
import br.edu.ifpr.repository.UserRoleRepository;
import br.edu.ifpr.repository.UsuarioRepository;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final UserRoleRepository userRoleRepository;
	private final PasswordEncoder passwordEncoder;
	private final ContatoRepository contatoRepository;
	
	public UsuarioService(UsuarioRepository usuarioRepository, UserRoleRepository userRoleRepository,
			PasswordEncoder passwordEncoder,
			ContatoRepository contatoRepository) {
		this.usuarioRepository = usuarioRepository;
		this.userRoleRepository = userRoleRepository;
		this.passwordEncoder = passwordEncoder;
		this.contatoRepository = contatoRepository;
	}

	public Iterable<Usuario> listarContatosNovos(String username) {
		//TODO Da pra melhorar :(
		Iterable<Usuario> usuarios = usuarioRepository.findAllBut(username);
		Iterable<Contato> contatosUsuario = contatoRepository.findAllByPrincipal(usuarioRepository.findByLogin(username));
		
		List<Usuario> contatosNovos = new ArrayList<>();

		for (Usuario usuario : usuarios) {
			boolean achei = false;
			
			//se o usuario da lista de usuario nao ta na lista de contatos 
			for (Contato contatoUsuario : contatosUsuario) {
				if(usuario.equals(contatoUsuario.getContato())) {
					achei = true;
					break;
				}
			}
			if(!achei) {
				contatosNovos.add(usuario);
			}
		}
		
		return contatosNovos;
	}

	public Usuario salvar(Usuario usuario) {
		//TODO conferir o login unique
		usuario
		.setSenha(passwordEncoder.encode(usuario.getSenha()))
		.setEnabled(true);

		userRoleRepository.save(new UserRole().setUsername(usuario.getLogin()).setAuthority("ROLE_USER"));

		return usuarioRepository.save(usuario);
	}
}