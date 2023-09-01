package com.webapp.researchhub.repository;


import com.webapp.researchhub.domain.MyUser;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface UserRepository extends CrudRepository<MyUser, Long> {
    MyUser findByEmail(String email);
    MyUser findByUsername(String username);
    Optional<MyUser> findById(Long id);

    ArrayList<MyUser> findAllByUsernameContains(String username);

    ArrayList<MyUser> findAllByFirstNameContains(String name);
    ArrayList<MyUser> findAllBySurnameContains(String name);
}
