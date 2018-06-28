package br.edu.ifpr.repository;

import org.springframework.data.repository.CrudRepository;

import br.edu.ifpr.domain.UserRole;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {

}
