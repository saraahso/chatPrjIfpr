package br.edu.ifpr.repository;

import org.springframework.data.repository.CrudRepository;

import br.edu.ifpr.domain.Contato;
import br.edu.ifpr.domain.Usuario;

public interface ContatoRepository extends CrudRepository<Contato, Long> {

	Iterable<Contato> findAllByPrincipal(Usuario principal);

	Contato findByPrincipalAndContato(Usuario principal, Usuario contato);

}
