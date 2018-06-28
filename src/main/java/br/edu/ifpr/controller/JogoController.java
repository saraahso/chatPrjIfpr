package br.edu.ifpr.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/jogo")
@ApiIgnore
public class JogoController {

	@RequestMapping(path = {"", "/", "/jogo"}, method = RequestMethod.GET)
	public String index(Model model, @AuthenticationPrincipal User usuarioLogado) {
		model.addAttribute("me" ,usuarioLogado.getUsername());

		return "jogo";
	}
}
