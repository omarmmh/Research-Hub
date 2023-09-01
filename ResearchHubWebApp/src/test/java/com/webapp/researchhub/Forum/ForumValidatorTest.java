package com.webapp.researchhub.Forum;

import com.webapp.researchhub.domain.Forum.ForumComment;
import com.webapp.researchhub.domain.Forum.ForumThread;
import com.webapp.researchhub.validator.ForumValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ForumValidatorTest {

    private ForumValidator validator;
    private ForumThread forumThread;
    private ForumComment forumComment;

    @BeforeEach
    void setUp() {
        validator = new ForumValidator();
        forumThread = new ForumThread();
        forumComment = new ForumComment();
    }

    @DisplayName("Test thread is invalid without a title and body of text")
    @Test
    public void testThreadNotValid() {
        forumThread.setTitle("");
        forumThread.setBody("");
        assertFalse(validator.validateThread(forumThread));
    }

    @DisplayName("Test thread is valid with a title and body of text")
    @Test
    public void testThreadIsValid() {
        forumThread.setTitle("Title");
        forumThread.setBody("A Body of text");
        assertTrue(validator.validateThread(forumThread));
    }

    @DisplayName("Test comment is valid with a body of text")
    @Test
    public void testCommentIsValid() {
        forumComment.setBody("A body of text");
        assertTrue(validator.validateComment(forumComment));
    }

    @DisplayName("Test comment is invalid without a body of text")
    @Test
    public void testCommentIsinValid() {
        forumComment.setBody("");
        assertFalse(validator.validateComment(forumComment));
    }
}
