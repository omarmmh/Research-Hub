package com.webapp.researchhub.repository;

import com.webapp.researchhub.domain.ProfilePic;
import org.springframework.data.repository.CrudRepository;

public interface ProfilePicRepository extends CrudRepository<ProfilePic, Long> {
    ProfilePic findByName(String name);
}
