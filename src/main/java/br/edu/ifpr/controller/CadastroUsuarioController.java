package br.edu.ifpr.controller;

import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpr.domain.Usuario;
import br.edu.ifpr.service.UsuarioService;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/cadastro")
@ApiIgnore
public class CadastroUsuarioController {

	private final UsuarioService chatService;
	
	public CadastroUsuarioController(UsuarioService chatService) {
		this.chatService = chatService;
	}

	@RequestMapping(path = "/novo", method = RequestMethod.GET)
	public String novo(Model model) {
		model.addAttribute("usuario", new Usuario());
		return "cadastro/novo";
	}
	
	@RequestMapping(path = "/salvar", method = RequestMethod.POST)
	public String salvar(@Valid Usuario usuario, RedirectAttributes redirectAttributes) {
		try {
			usuario = chatService.salvar(usuario);
		} catch(DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("erro", "Usuário " + usuario.getLogin() + " já existe.");
			return "redirect:/cadastro/novo";
		}
		
		redirectAttributes.addFlashAttribute("mensagem", "Usuário " + usuario.getLogin() + " cadastrado com sucesso.");
		
		return "redirect:/login";
	}
}
