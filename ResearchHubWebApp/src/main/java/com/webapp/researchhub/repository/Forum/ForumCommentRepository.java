package com.webapp.researchhub.repository.Forum;

import com.webapp.researchhub.domain.Forum.ForumComment;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ForumCommentRepository extends CrudRepository<ForumComment, Long> {
}
