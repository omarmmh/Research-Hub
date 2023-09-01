package com.webapp.researchhub.repository;

import com.webapp.researchhub.domain.Account;
import com.webapp.researchhub.domain.MyUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {
    Optional<Account> findById(Long id);
}
