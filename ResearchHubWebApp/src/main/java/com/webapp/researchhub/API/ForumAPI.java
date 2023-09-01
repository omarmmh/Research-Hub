package com.webapp.researchhub.API;

import com.webapp.researchhub.domain.Forum.DTO.ThreadCommentDto;
import com.webapp.researchhub.domain.Forum.ForumCategory;
import com.webapp.researchhub.domain.Forum.ForumComment;
import com.webapp.researchhub.domain.Forum.ForumThread;
import com.webapp.researchhub.domain.Forum.DTO.UpdateThreadDto;
import com.webapp.researchhub.domain.Forum.ForumVote;
import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.repository.Forum.ForumCategoryRepository;
import com.webapp.researchhub.repository.Forum.ForumCommentRepository;
import com.webapp.researchhub.repository.Forum.ForumThreadRepository;
import com.webapp.researchhub.repository.Forum.ForumVoteRepository;
import com.webapp.researchhub.repository.UserRepository;
import com.webapp.researchhub.validator.ForumValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * FORUM API For Serving and Consuming the AJAX requests on the front-end.
 */
@RestController
@RequestMapping("/api/forum")
public class ForumAPI {

    @Autowired
    ForumCategoryRepository forumCategoryRepository;
    @Autowired
    ForumThreadRepository forumThreadRepository;
    @Autowired
    ForumCommentRepository forumCommentRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ForumVoteRepository forumVoteRepository;

    private ForumValidator validator = new ForumValidator();


    /****************** Updating Data ******************/

    /**
     * Updates an existing thread and saves it to the database.
     *
     * @param updateThreadDto : Data transfer object.  Captures the javascript model being sent and saves it as a java object.
     * @param id              : Id of the thread being updated.
     * @return : Http Status, 200 OK, with success message, or Http Status, 418 I_AM_A_TEAPOT, with error message.
     */
    @PostMapping("/edit-thread")
    public ResponseEntity<String> updateThread(@ModelAttribute("model") UpdateThreadDto updateThreadDto, @RequestParam Long id, Principal principal) {
        ForumThread thread = forumThreadRepository.findById(id).get();
        thread.setTitle(updateThreadDto.getTitle());
        thread.setBody(updateThreadDto.getBody());
        thread.setDateModified(new Date());

        if (!validator.isOwner(thread, principal)) {
            return new ResponseEntity<>("Unauthorised", HttpStatus.UNAUTHORIZED);
        }
        if (!validator.validateThread(thread)) {
            return new ResponseEntity<>("empty fields", HttpStatus.BAD_REQUEST);
        }
        forumThreadRepository.save(thread);
        return new ResponseEntity<>("Thread " + id + ": update successful.", HttpStatus.OK);
    }


    /**
     * @param id :Thread ID
     * @return : The thread object found by ID.
     */
    @GetMapping("/get-thread")
    public ResponseEntity<UpdateThreadDto> getThread(@RequestParam Long id) {
        // grabbing thread incase update is cancelled
        ForumThread thread = forumThreadRepository.findById(id).get();
        UpdateThreadDto originalThread = new UpdateThreadDto(thread.getTitle(), thread.getBody());
        return new ResponseEntity<>(originalThread, HttpStatus.OK);
    }

    /**
     * Updates selected comment and saves it to the database
     *
     * @param id
     * @param body
     * @return : Http Status, 200 OK, with success message, or Http Status, 418 I_AM_A_TEAPOT, with error message.
     */
    @PostMapping("/edit-comment")
    public ResponseEntity<String> updateComment(@RequestParam Long id, @RequestParam String body, Principal principal) {

        ForumComment comment = forumCommentRepository.findById(id).get();
        comment.setBody(body);

        if (!validator.isOwner(comment, principal)) {
            return new ResponseEntity<>("Unauthorised", HttpStatus.UNAUTHORIZED);
        }
        if (body.isBlank()) {
            return new ResponseEntity<>("Empty body", HttpStatus.BAD_REQUEST);
        }
        // Save the updates.
        forumCommentRepository.save(comment);

        // Return success back to javascript / client.
        return new ResponseEntity<>("Comment " + comment.getId() + ": update successful.", HttpStatus.OK);
    }

    /**
     * @param id : Comment ID
     * @return : The comment object found by ID, with status code 200 OK.
     */
    @GetMapping("/get-comment")
    public ResponseEntity<Map<String, Object>> getComment(@RequestParam Long id) {
        ForumComment comment = forumCommentRepository.findById(id).get();
        Map<String, Object> map = new HashMap<>();
        map.put("id", comment.getId());
        map.put("body", comment.getBody());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }


    /****************** Writing Data ******************/

    /**
     * @param threadCommentDto : The DTO Class that maps the JavaScript Model to our Entity Model.
     * @param principal        : Represents a user's identity
     * @return : A Hash Map (JSON) response to the client containing the new comments Id, and the users full name who
     * created the comment.
     */
    @PostMapping("/add-comment")
    public ResponseEntity<Map<String, Object>> addComment(@ModelAttribute("data") ThreadCommentDto threadCommentDto, Principal principal) {
        MyUser currentUser = userRepository.findByEmail(principal.getName());
        ForumThread forumThread = forumThreadRepository.findById(threadCommentDto.getThreadId()).get();

        ForumComment forumComment = new ForumComment();
        forumComment.setBody(threadCommentDto.getBody());
        forumComment.setUser(currentUser);
        forumComment.setThread(forumThreadRepository.findById(threadCommentDto.getThreadId()).get());
        forumThread.getCommentList().add(forumComment);

        if (!validator.validateComment(forumComment)) {
            Map<String, Object> error = new HashMap<>();
            error.put("Error", "invalid input");
            return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
        }

        forumComment = forumCommentRepository.save(forumComment);

        Map<String, Object> map = new HashMap<>();
        map.put("commentId", forumComment.getId());
        map.put("UserName", currentUser.getUsername());
        SimpleDateFormat tmp = new SimpleDateFormat("dd-MM-yyyy");
        String fdate = tmp.format(forumComment.getDateCreated());
        map.put("date", fdate);
        return new ResponseEntity<>(map, HttpStatus.CREATED);
    }

    /**
     * @param cat       : the javascript mapped category to be saved.
     * @param principal : represents the user's identity
     * @return : A Hash Map (JSON) client containing the new category Id, and the users full name who
     * created the comment.
     */
    @PostMapping("/add-category")
    public Map<String, Object> createNewCategory(@ModelAttribute ForumCategory cat, Principal principal) {
        MyUser currentUser = userRepository.findByEmail(principal.getName());
        if (!validator.validateCategory(cat, principal)) {
            Map<String, Object> error = new HashMap<>();
            error.put("Error", "invalid input");
            return error;
        }

        cat.setUser(userRepository.findByEmail(principal.getName()));
        forumCategoryRepository.save(cat);

        Map<String, Object> map = new HashMap<>();
        map.put("catId", cat.getId());
        map.put("UserName", currentUser.getFullName());

        return map;
    }


    /****************** Deleting Data ******************/

    /**
     * Deletes the Forum Category with the given ID from the database
     *
     * @param id : the ID to be deleted
     * @return : Http Status OK
     */
    @PostMapping("/delete-category")
    public HttpStatus deleteCat(@RequestParam Long id) {
        forumCategoryRepository.deleteById(id);
        return HttpStatus.OK;
    }

    /**
     * Deletes the Forum Thread with the given ID from the database`
     *
     * @param id : the ID to be deleted
     * @return : Http Status OK
     */
    @PostMapping("/delete-thread")
    public HttpStatus deleteThread(@RequestParam Long id) {
        forumThreadRepository.deleteById(id);
        return HttpStatus.OK;
    }

    /**
     * Deletes the Forum Comment with the given ID from the database
     *
     * @param id : the ID to be deleted
     * @return : Http Status OK
     */
    @PostMapping("/delete-comment")
    public HttpStatus deleteComment(@RequestParam Long id) {
        ForumComment c = forumCommentRepository.findById(id).get();
        ForumThread t = forumThreadRepository.findById(c.getThread().getId()).get();
        c.setThread(null);
        t.getCommentList().remove(c);
        forumCommentRepository.delete(c);
        return HttpStatus.OK;
    }

    /**
     * Mapping to handle the up/down vote system
     * @param id : Comment ID.
     * @param type : The type of vote being submitted, either up or down.
     * @param principal : The principal object passed by spring container.
     * @return : HTTP Response with error, success, vote count, and vote type details in a hash map, parsed to JSON.
     */
    @PostMapping("/votes")
    public ResponseEntity<String> castVote(@RequestParam Long id, @RequestParam String type, Principal principal) {
        MyUser user = userRepository.findByEmail(principal.getName());
        ForumComment comment = forumCommentRepository.findById(id).get();
        ForumThread thread = forumThreadRepository.findById(comment.getThread().getId()).get();
        ForumVote v = forumVoteRepository.findByUserAndThread(user, thread);

        HashMap response = new HashMap();

        if(thread.getVotedUsers().contains(user) && !comment.getVoteList().contains(v)) {
            response.put("error", "You have already cast a vote on another comment");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }

        for (MyUser u : thread.getVotedUsers()) {
            if (u.getEmail().equals(principal.getName())) {
                // User has voted and is trying to vote on another comment.  reject
                // User has voted check the type as we can change the vote.
                if (v.getType().equals(type)) {
                    response.put("error", "You have already cast a vote on this comment");
                    return new ResponseEntity(response, HttpStatus.CONFLICT);
                } else {
                    // Different Vote, for example, original vote was up voted, but now they down vote.
                    if ((v.getType().equals("up") && type.equals("down")) || v.getType().equals("down") && type.equals("up")) {
                        // Cancel the vote.
                        thread.getVotedUsers().remove(user);
                        forumThreadRepository.save(thread);
                        comment.getVoteList().remove(v);
                        forumVoteRepository.delete(v);
                        forumCommentRepository.save(comment);
                        response.put("voteType", "cancel");
                        response.put("count", comment.getVoteCount());
                        response.put("success", "You have cancelled your vote.");
                        return new ResponseEntity(response, HttpStatus.ACCEPTED);
                    }
                }
            }
        }

        ForumVote forumVote = new ForumVote();
        forumVote.setUser(user);
        forumVote.setType(type);
        forumVote.setComment(comment);
        forumVote.setThread(thread);
        comment.getVoteList().add(forumVote);

        thread.getVotedUsers().add(user);

        forumCommentRepository.save(comment);
        forumThreadRepository.save(thread);

        response.put("voteType", type);
        response.put("success", "A vote has been cast.");
        response.put("count", comment.getVoteCount());

        return new ResponseEntity(response, HttpStatus.ACCEPTED);
    }
}