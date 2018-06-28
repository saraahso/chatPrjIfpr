package br.edu.ifpr.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import br.edu.ifpr.domain.Mensagem;
import br.edu.ifpr.domain.Usuario;

public interface MensagemRepository extends PagingAndSortingRepository<Mensagem, Long> {

	Iterable<Mensagem> findAllByDestinatarioAndEmissorOrderByDataEnvio(Usuario destinatario, Usuario emissor);

	@Query("select count(m) from Mensagem m where m.lida = false and m.emissor.login = :loginEmissor and m.destinatario.login = :loginDestinatario")
	Integer recuperaQuantidadeMensagensNaoLidas(@Param("loginEmissor") String loginEmissor, @Param("loginDestinatario") String loginDestinatario);

}
