package com.webapp.researchhub.validator;


import com.webapp.researchhub.domain.Forum.ForumBaseEntity;
import com.webapp.researchhub.domain.Forum.ForumCategory;
import com.webapp.researchhub.domain.Forum.ForumComment;
import com.webapp.researchhub.domain.Forum.ForumThread;
import com.webapp.researchhub.repository.Forum.ForumCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.Principal;
import java.util.ArrayList;

/**
 * Handles all validation of the forum posts
 * - categories must have a title, body, & must have a unique title.
 * - threads must have a title, body, & the title must be unique.
 * - comments must have a body.
 */


public class ForumValidator  {

    @Autowired private ForumCategoryRepository forumCategoryRepo;

    public boolean isOwner(ForumBaseEntity target, Principal principal) {
        // Split the principal check into another method so that we can check
        // for unauthorised attempts and specifically send a 401 rather than a generic error.
        return target.getUser().getEmail().equals(principal.getName());
    }

    private boolean validateBody(ForumBaseEntity target) {
        if(target.getBody().isBlank()) {
            return false;
        }
        return true;
    }

    private boolean validateTitle(ForumThread target) {
        if(target.getTitle().isBlank()) {
            return false;
        }
        return true;
    }

    /**
     * Validation method for generated threads.
     * - threads must have a title, body, & the title must be unique.
     * @param target The thread that will be validated
     * @return true if the thread is valid, false if not.
     */
    public boolean validateThread(ForumThread target) {
        if(!validateBody(target)) {
            return false;
        }
        if(!validateTitle(target)) {
            return false;
        }
        return true;
    }

    /**
     * Validation method for generated comments
     * - comments must have a body.
     * @param target The target that will be validated.
     * @return true if the comment is valid, false if not.
     */
    public boolean validateComment(ForumComment target) {
        if(!validateBody(target)) {
            return false;
        }
        return true;
    }

    /**
     * Validation method for generated categories.
     * - categories must have a title, body, & must have a unique title.
     * @param target The category that will be validated
     * @return true if the thread is valid, false if not.
     */
    public boolean validateCategory(ForumCategory target, Principal principal) {
        if(target.getTitle().isBlank()) {
            return false;
        }

        ArrayList<ForumCategory> allCats = forumCategoryRepo.findAll();
        for(ForumCategory cat : allCats) {
            if(cat.getTitle().equals(target.getTitle())) {
                return false;
            }
        }
        return true;
    }
}
