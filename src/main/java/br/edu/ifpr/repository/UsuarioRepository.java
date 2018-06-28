package br.edu.ifpr.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.edu.ifpr.domain.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

	Usuario findByLogin(String login);

	@Query("select u from Usuario u where u.login <> :username")
	Iterable<Usuario> findAllBut(@Param("username") String username);

	@Query("select c.contato from Contato c where c.principal = :principal")
	Iterable<Usuario> findAllByContatoPrincipal(@Param("principal") Usuario principal);

}
