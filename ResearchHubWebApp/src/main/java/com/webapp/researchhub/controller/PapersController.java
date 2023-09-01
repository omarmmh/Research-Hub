package com.webapp.researchhub.controller;

import com.webapp.researchhub.domain.Account;
import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.UserFile;
import com.webapp.researchhub.repository.FileRepository;
import com.webapp.researchhub.repository.UserRepository;
import com.webapp.researchhub.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class PapersController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileService service;

    /***
     * Gets the current user logged in to present all the papers they have in the html page.
     * @param model Adds to the model.
     * @param principal Get current logged in user.
     * @return The html page.
     * @author jwm22
     */
    @RequestMapping("/myPapers")
    public String viewMyPapers (Model model, Principal principal){
        //Gets the user currently logged in
        MyUser user = userRepository.findByEmail(principal.getName());
        //Get user papers
        List<UserFile> papers = user.getUserFiles();
        Collections.reverse(papers);

        model.addAttribute("ownProfile", true);
        model.addAttribute("user", user);
        model.addAttribute("userPapers", papers);

        return "files/viewPapers";
    }

    /***
     * Lists all the papers for a specified user.
     * @param model : The model.
     * @param principal : The currently logged-in user.
     * @param username : The username of the user whose papers are viewed.
     * @return : View of specified user's papers.
     */
    @RequestMapping("/papers/{username}")
    public String viewPapers (Model model, Principal principal, @PathVariable("username") String username){
        // Gets the user by the specified username.
        MyUser user= userRepository.findByUsername(username);

        // If the user doesn't exist, shows an error page.
        if (user == null){
            return "error";
        }
        // If the user is the currently logged-in user, shows logged-in user their own papers.
        if (principal != null && user.getEmail().equals(principal.getName())){
            return "redirect:/myPapers";
        }

        // Gets the account for the user and checks papers privacy settings.
        // If the user is not allowed to see the papers, they're sent to the access-denied page.
        Account account= user.getAccount();
        switch(account.getDisplayPapers()) {
            case "none":
                return "access-denied";
            case "loggedin":
                if (principal == null) {
                    return "access-denied";
                }
        }

        //Gets the user papers.
        List<UserFile> papers = user.getUserFiles();
        Collections.reverse(papers);

        model.addAttribute("ownProfile", false);
        model.addAttribute("user", user);
        model.addAttribute("userPapers", papers);

        return "files/viewPapers";
    }

    /***
     * Gets the file from repository and gets the bytes to be opened in a new window in the browser
     * (.body() method does this).
     * @param id The id of the file wanting to be viewed
     * @return The content of the PDF file.
     * @author jwm22
     */
    @GetMapping("/paper/{id}")
    public ResponseEntity<?> viewPaper(@PathVariable Long id){
        Optional<UserFile> file = fileRepository.findById(id);
        byte[] data = file.get().getData();
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("application/pdf")).body(data);
    }
    @GetMapping("/delete/{id}")
    public String deletePaper(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("result", "deleted");
        service.delete(id, principal);
        return "redirect:/myPapers";
    }

}
