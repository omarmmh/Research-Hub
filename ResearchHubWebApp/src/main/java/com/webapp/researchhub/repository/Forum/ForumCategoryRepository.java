package com.webapp.researchhub.repository.Forum;

import com.webapp.researchhub.domain.Forum.ForumCategory;
import com.webapp.researchhub.domain.Forum.ForumThread;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ForumCategoryRepository extends CrudRepository<ForumCategory, Long> {
    ArrayList<ForumCategory> findAll();
    ForumCategory findByTitle(String title);
}
