package com.webapp.researchhub.repository.Forum;

import com.webapp.researchhub.domain.Forum.ForumThread;
import com.webapp.researchhub.domain.Forum.ForumVote;
import com.webapp.researchhub.domain.MyUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ForumVoteRepository extends  CrudRepository <ForumVote, Long> {
    List<ForumVote> findAllByComment_Id(Long id);
    List<ForumVote> findAllByThread(ForumThread thread);
    ForumVote findByUserAndThread(MyUser user, ForumThread thread);
}