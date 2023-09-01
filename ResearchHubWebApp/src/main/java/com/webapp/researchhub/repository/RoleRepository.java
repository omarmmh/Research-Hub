package com.webapp.researchhub.repository;


import com.webapp.researchhub.domain.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, String> {
    Role findByName(String name);
}
