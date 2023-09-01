package com.webapp.researchhub.controller;

import com.webapp.researchhub.domain.Account;
import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.Role;
import com.webapp.researchhub.repository.AccountRepository;
import com.webapp.researchhub.repository.UserRepository;
import com.webapp.researchhub.validator.PasswordStrengthValidator;
import jakarta.validation.Valid;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@Controller
public class AccountCreationController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder pe;


/**
 * showAccountCreationForm method handles GET request & returns the view security/account-creation-form
 * MyUser allows the user to fill out the form
 * addNewAccount method deals with POST to /account-creation which handle the form data
 * Password is being checked. if the password & confirmPassword match & accept if not return "passwords don't match"
 * findByEmail check if email already exists in database & if exists it returns message "Email already exists"

 * */

    @GetMapping("/account-creation")
    public String showAccountCreationForm(Model model){
        model.addAttribute( "user", new MyUser());
        return "security/account-creation-form";
    }

    @PostMapping("/account-creation")
    public String addNewAccount(@Valid @ModelAttribute("user") MyUser user, BindingResult result,
                                @RequestParam String confirmPassword){

        PasswordStrengthValidator psv = new PasswordStrengthValidator();
        PasswordValidator validator = psv.getValidator();

        // Checks if password and confirm password are the same
        if(!user.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.user", "Passwords don't match");
        }

        // Checks if the email is already in the database
        if(userRepository.findByEmail(user.getEmail())!=null){
            result.rejectValue("email", "error.user", "Email already exists");
        }

        // Checks if the username is not already in the database
        if(userRepository.findByUsername(user.getUsername())!=null){
            result.rejectValue("username", "error.user", "Username already exists");
        }

        // checks if the password meets the password requirements
        RuleResult ruleResult = validator.validate(new PasswordData(user.getPassword()));
        if(!ruleResult.isValid()){
            List<String> messages = validator.getMessages(ruleResult);
            for (String s : messages){
                result.rejectValue("password", "error.user", s);
            }
        }


        if (result.hasErrors()){
            System.out.println(result.toString());
            return "security/account-creation-form";
        }

        Role role = new Role("USER");
        user.getRoles().add(role);
        user.setPassword(pe.encode(user.getPassword()));
        Account account = new Account();
        user.setAccount(account);
        accountRepository.save(account);
        userRepository.save(user);
        return "security/login-form"; /* updated */
    }
}
