package com.webapp.researchhub.controller;

import com.webapp.researchhub.domain.Forum.ForumCategory;
import com.webapp.researchhub.domain.Forum.ForumComment;
import com.webapp.researchhub.domain.Forum.ForumThread;
import com.webapp.researchhub.domain.Forum.ForumVote;
import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.repository.Forum.ForumCategoryRepository;
import com.webapp.researchhub.repository.Forum.ForumThreadRepository;
import com.webapp.researchhub.repository.Forum.ForumVoteRepository;
import com.webapp.researchhub.repository.UserRepository;
import com.webapp.researchhub.validator.ForumValidator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.*;


@Controller
public class ForumController {
    @Autowired
    private ForumCategoryRepository forumCategoryRepo;

    @Autowired
    private ForumThreadRepository forumThreadRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ForumVoteRepository forumVoteRepository;

    /****************** Reading Data ******************/

    /**
     * outputs all categories into the forum/category-list file.
     *
     * @param model
     * @return
     */
    @GetMapping("/forum")
    public String forum(Model model) {
        ArrayList<ForumCategory> categories = forumCategoryRepo.findAll();
        model.addAttribute("categories", categories);
        return "forum/category-list";
    }

    /**
     * outputs all threads of the input category into the forum/thread-list file.
     *
     * @param model
     * @param catId
     * @return
     */
    @GetMapping("/forum/{catId}")
    public String displayThreads(HttpServletRequest request, Model model, @PathVariable Long catId) {
        ArrayList<ForumThread> threads = forumThreadRepo.findAllByCategory_Id(catId);
        Optional<ForumCategory> category = forumCategoryRepo.findById(catId);
        model.addAttribute("threads", threads);
        model.addAttribute("category", category.get());

        // As we cannot detect if the user has tampered with the data client side, we will Save the name of the category
        // to the user session.  If a user then decides to create a new thread, we can retrieve the category from the user
        // session that we have set and retrieve it when needed in a Post mapping.
        request.getSession().setAttribute("category", category.get());

        model.addAttribute("newThread", new ForumThread());

        return "forum/thread-list";
    }

    /**
     *
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/forum/{catId}/thread/{id}")
    public String displayComments(Model model, @PathVariable Long catId, @PathVariable Long id, Principal principal, HttpServletRequest request) {
        ForumThread thread = forumThreadRepo.findById(id).get();
        List<ForumVote> voteList = forumVoteRepository.findAllByThread(thread);

        for(ForumVote v: voteList) {
            if(v.getUser().getEmail().equals(principal.getName())) {
                // User has cast a vote in this thread.
                model.addAttribute("voteType", v.getType());
                model.addAttribute("commentId", v.getComment().getId());
            }
        }

        thread.incrementPageViewCount();
        thread = forumThreadRepo.save(thread);
        // Sort list based on up vote order.
        thread.getCommentList().sort(Comparator.comparing(ForumComment::getVoteCount, Comparator.reverseOrder()));
        model.addAttribute("thread", thread);
        model.addAttribute("returnLink", request.getHeader("Referer"));
        return "forum/thread-detail";
    }

    /**
     * Lists all the threads for a logged-in User.
     * @param model : The Model.
     * @param principal : The currently logged-in User.
     * @return : View of logged-in User's threads.
     */
    @GetMapping("/myThreads")
    public String viewMyThreads(Model model, Principal principal){
        MyUser user= userRepo.findByEmail(principal.getName());
        ArrayList<ForumThread> threads= forumThreadRepo.findAllByUser(user);
        Collections.reverse(threads);

        model.addAttribute("threads", threads);
        return "forum/viewThreads";
    }

    /****************** Writing Data ******************/


    /**
     * Creates a new thread
     * @param thread : Thread object
     * @param request : The HTTP request
     * @param principal : The Principle
     * @return : Thread view.
     */
    @PostMapping("/forum/create-thread")
    public String createNewThread(@ModelAttribute ForumThread thread, HttpServletRequest request, Principal principal) {
        ForumValidator validator = new ForumValidator();
        if(!validator.validateThread(thread)) {
            return "/site/index";
        }
        ForumCategory category = (ForumCategory) request.getSession().getAttribute("category");
        thread.setCategory(category);
        thread.setUser(userRepo.findByEmail(principal.getName()));
        forumThreadRepo.save(thread);
        request.getSession().setAttribute("category", null);
        return "redirect:/forum/" + category.getId() + "/thread/" + thread.getId();
    }
}