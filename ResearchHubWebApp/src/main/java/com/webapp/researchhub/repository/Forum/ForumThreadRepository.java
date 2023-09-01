package com.webapp.researchhub.repository.Forum;

import com.webapp.researchhub.domain.Forum.ForumThread;
import com.webapp.researchhub.domain.MyUser;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Date;

public interface ForumThreadRepository extends CrudRepository<ForumThread, Long> {
    ArrayList<ForumThread> findAllByUser(MyUser user);
    ArrayList<ForumThread> findTop3ByUserAndDateModifiedBeforeOrderByDateModifiedDesc(MyUser user, Date date);
    ArrayList<ForumThread> findAllByCategory_Id(Long Id);
    ArrayList<ForumThread> findAllByTitleContains(String title);
    ArrayList<ForumThread> findAll();
}
