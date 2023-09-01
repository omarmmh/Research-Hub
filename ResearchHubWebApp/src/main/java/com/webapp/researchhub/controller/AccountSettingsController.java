package com.webapp.researchhub.controller;

import com.webapp.researchhub.domain.Account;
import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.ProfilePic;
import com.webapp.researchhub.repository.AccountRepository;
import com.webapp.researchhub.repository.ProfilePicRepository;
import com.webapp.researchhub.repository.UserRepository;
import com.webapp.researchhub.service.FileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

@Controller
public class AccountSettingsController {
    @Autowired
    UserRepository urepo;
    @Autowired
    AccountRepository arepo;
    @Autowired
    ProfilePicRepository prepo;

    @Autowired
    FileService fservice;

    @GetMapping("/account-settings")
    public String showSettings(Model model, Principal principal){
        MyUser user= urepo.findByEmail(principal.getName());


        model.addAttribute("user", user);
        model.addAttribute("account", user.getAccount());
        // Create Base64 String of pfp data to be able to display pfp through html.
        if (user.getProfilePic() != null) {
            model.addAttribute("pfpBase64", String.format("data:image/png;base64,%s", Base64.getEncoder().encodeToString(user.getProfilePic().getData())));
        }
        return "account/account-settings";
    }

    @PostMapping(value="/account-settings", params="type=detailsSubmit")
    public String submitAccountDetails(@ModelAttribute("user") MyUser updatedUser, @RequestParam("pfpFile") MultipartFile pfpFile,
                                       Principal principal, BindingResult result) throws IOException {
        MyUser user = urepo.findByEmail(principal.getName());

        // Only update a user's profile picture (pfp) if the user uploaded an image file.
        if (!pfpFile.isEmpty() && (pfpFile.getContentType().equals("image/png") || pfpFile.getContentType().equals("image/jpeg"))) {

            // If the user already has an own pfp saved, delete the old pfp.
            // Do not delete anything if the user has the default pfp (id= 1).
            if (user.getProfilePic() != null) {
                ProfilePic oldPfp= user.getProfilePic();
                // Delete pfp from database.
                user.setProfilePic(null);
                prepo.delete(oldPfp);
            }

            // Upload the new pfp file to the database
            fservice.Upload(pfpFile, user, null);

        }

        // username is rejected when the username is updated and it still exists in the database
        if(!user.getUsername().equals(updatedUser.getUsername()) && urepo.findByUsername(updatedUser.getUsername())!=null){
            result.rejectValue("username", "error.user", "Username already exists");
        }

        // email is rejected when the email is updated and the updated email already exists in the database
        if(!user.getEmail().equals(updatedUser.getEmail()) && urepo.findByEmail(updatedUser.getEmail())!=null){
            result.rejectValue("email", "error.user", "Email already exists");
        }

        if (result.hasErrors()){
            return "account/account-settings";
        }


        user.setFirstName(updatedUser.getFirstName());
        user.setSurname(updatedUser.getSurname());
        user.setBio(updatedUser.getBio());
        user.setCountry(updatedUser.getCountry());
        user.setResearchFacility(updatedUser.getResearchFacility());
        user.setUsername(updatedUser.getUsername());
        // user.setEmail(updatedUser.getEmail());

        urepo.save(user);
        return "redirect:/account-settings";
    }

    @GetMapping(value="/account-settings", params="type=removePfp")
    public String removePfp(Principal principal) {
        MyUser user = urepo.findByEmail(principal.getName());

        // If the user has an own pfp saved, delete the pfp.
        // Do not delete anything if the user has the default pfp (id= 1).
        if (user.getProfilePic() != null) {
            ProfilePic oldPfp= user.getProfilePic();

            // Set user's pfp to default pfp and delete old pfp from the database.
            user.setProfilePic(null);
            prepo.delete(oldPfp);
            urepo.save(user);
        }
        return "redirect:/account-settings";
    }

    /*
    @PostMapping(value="/account-settings", params="privacySubmit")
    public String submitAccountPrivacy(@Valid @ModelAttribute("account") Account updatedAccount, BindingResult result,  Principal principal){
        Account account = urepo.findByEmail(principal.getName()).getAccount();

        account.setDisplayName(updatedAccount.getDisplayName());
        account.setDisplayEmail(updatedAccount.getDisplayEmail());
        account.setDisplayPapers(updatedAccount.getDisplayPapers());
        account.setDisplayForumActivity(updatedAccount.getDisplayForumActivity());
        account.setDisplayCountry(updatedAccount.getDisplayCountry());
        account.setDisplayResearchFacility(updatedAccount.getDisplayResearchFacility());
        arepo.save(account);
        return "account/account-settings";
    }

    @PostMapping(value="/account-settings", params="emailNotificationsSubmit")
    public String submitEmailNotifications(@Valid @ModelAttribute("account") Account updatedAccount, BindingResult result, Principal principal){
        Account account = urepo.findByEmail(principal.getName()).getAccount();

        account.setEmailOnForumReply(updatedAccount.isEmailOnForumReply());
        account.setEmailOnForumMessageUpvoted(updatedAccount.isEmailOnForumMessageUpvoted());
        account.setEmailOnNewForumPost(updatedAccount.isEmailOnNewForumPost());
        arepo.save(account);
        return "redirect:/account-settings";
    }

    @PostMapping(value="/account-settings", params="deleteSubmit")
    public String submitDeleteAccount(Principal principal){
        MyUser user = urepo.findByEmail(principal.getName());
        urepo.delete(user);

        return "redirect:/";
    }
    */
}
