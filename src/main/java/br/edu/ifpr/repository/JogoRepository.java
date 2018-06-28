package br.edu.ifpr.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import br.edu.ifpr.domain.Jogo;
import br.edu.ifpr.domain.Usuario;

public interface JogoRepository extends PagingAndSortingRepository<Jogo, Long> {

	Iterable<Jogo> findAllByDestinatarioAndEmissorOrderByJogada(Usuario destinatario, Usuario emissor);


}