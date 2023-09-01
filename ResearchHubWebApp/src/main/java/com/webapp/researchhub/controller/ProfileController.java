package com.webapp.researchhub.controller;

import com.webapp.researchhub.domain.Account;
import com.webapp.researchhub.domain.Forum.ForumThread;
import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.UserFile;
import com.webapp.researchhub.domain.calendar.Event;
import com.webapp.researchhub.domain.calendar.EventInterest;
import com.webapp.researchhub.repository.AccountRepository;
import com.webapp.researchhub.repository.Calendar.EventRepository;
import com.webapp.researchhub.repository.FileRepository;
import com.webapp.researchhub.repository.Forum.ForumThreadRepository;
import com.webapp.researchhub.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.*;

@Controller
public class ProfileController {
    @Autowired
    UserRepository userRepo;
    @Autowired
    EventRepository eventRepo;
    @Autowired
    FileRepository fileRepo;
    @Autowired
    ForumThreadRepository forumRepo;


    /***
     * Shows the profile for the logged-in User.
     * @param model : The Model.
     * @param principal : The currently logged-in user.
     * @return : View of logged-in User's profile.
     */
    @GetMapping("/myProfile")
    public String showMyProfile(Model model, Principal principal, HttpServletRequest request) {
        String baseUrl = ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();

        if (request.getHeader("Referer").equals(baseUrl + "/login-user")) {
            model.addAttribute("login", true);
        }

        // Today's date
        Date date = new Date();

        // Gets the user by the logged-in User.
        MyUser user= userRepo.findByEmail(principal.getName());

        // Events
        List<Event> events= eventRepo.findTop4ByOrganiserAndStartDateAfterOrderByStartDateAsc(user, date);
        for (EventInterest e: user.getEventInterestList()) {
            if (e.getEvent().getStartDate().after(date)){
                events.add(e.getEvent());
            }
        }

        Collections.sort(events, Comparator.comparing(Event::getStartDate));
        events= (events.size()>3) ? events.subList(0, 4) : events;

        // Threads and Papers
        List<UserFile> papers= fileRepo.findTop3ByUserAndDateUploadedBeforeOrderByDateUploadedDesc(user, date);
        List<ForumThread> threads= forumRepo.findTop3ByUserAndDateModifiedBeforeOrderByDateModifiedDesc(user, date);

        // Specifies if the currently logged-in user is viewing their own page or not and adds values to the model.
        model.addAttribute("ownProfile", true);
        model.addAttribute("user", user);
        model.addAttribute("events", events);
        model.addAttribute("papers", papers);
        model.addAttribute("threads", threads);

        // If the User has a profile picture saved, gets its base64 value and adds it to the model.
        model.addAttribute("pfpBase64",
                (user.getProfilePic() != null)
                        ? String.format("data:image/png;base64,%s", Base64.getEncoder().encodeToString(user.getProfilePic().getData()))
                        : null);
        return "profile/profile";
    }

    /***
     * Shows the profile for a specified user.
     * @param model : The model.
     * @param principal : The currently logged-in user.
     * @param username : The username of the user whose profile is viewed.
     * @return : View of specified user's profile.
     */
    @GetMapping("/profile/{username}")
    public String showUserProfile(Model model,Principal principal, @PathVariable("username") String username) {
        // Gets the user by the username.
        MyUser user= userRepo.findByUsername(username);

        // If the user doesn't exist, shows an error page.
        if (user == null){
            return "error";
        }
        // If the user is the currently logged-in user, shows logged-in user their own papers.
        if (principal != null && user.getEmail().equals(principal.getName())){
            return "redirect:/myProfile";
        }

        // Today's date
        Date date = new Date();
        List<UserFile> papers= fileRepo.findTop3ByUserAndDateUploadedBeforeOrderByDateUploadedDesc(user, date);

        // Gets the user's account.
        Account account= user.getAccount();
        // Handels the research papers privacy.
        switch(account.getDisplayPapers()) {
            case "none":
                user.setUserFiles(null);
                break;
            case "loggedin":
                if (principal == null) {
                    user.setUserFiles(null);
                    break;
                }
        }
        // Handels the email privacy.
        switch(account.getDisplayEmail()) {
            case "none":
                user.setEmail(null);
                break;
            case "loggedin":
                if (principal == null) {
                    user.setEmail(null);
                    break;
                }
        }
        // Handels the full name privacy.
        switch(account.getDisplayName()) {
            case "none":
                user.setFirstName("");
                user.setSurname("");
                break;
            case "loggedin":
                if (principal == null) {
                    user.setFirstName("");
                    user.setSurname("");
                    break;
                }
        }
        // Handels the country privacy.
        switch(account.getDisplayCountry()) {
            case "none":
                user.setCountry("");
                break;
            case "loggedin":
                if (principal == null) {
                    user.setCountry("");
                    break;
                }
        }
        // Handels the research facility privacy.
        switch(account.getDisplayResearchFacility()) {
            case "none":
                user.setResearchFacility("");
                break;
            case "loggedin":
                if (principal == null) {
                    user.setResearchFacility("");
                    break;
                }
        }

        // Specifies if the currently logged-in user is viewing their own page or not and adds values to the model.
        model.addAttribute("ownProfile", false);
        model.addAttribute("user", user);
        model.addAttribute("papers", papers);

        // If the User has a profile picture saved, gets its base64 value and adds it to the model.
        model.addAttribute("pfpBase64",
                (user.getProfilePic() != null)
                        ? String.format("data:image/png;base64,%s", Base64.getEncoder().encodeToString(user.getProfilePic().getData()))
                        : null);
        return "profile/profile";
    }
}
