package com.webapp.researchhub.controller;

import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.UserFile;
import com.webapp.researchhub.repository.UserRepository;
import com.webapp.researchhub.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.security.Principal;


@Controller
@ControllerAdvice
public class UploadController{

    @Autowired
    private FileService service;

    @Autowired
    private UserRepository userRepository;

    /***
     * Passes in the user file so that the title, authors and description would be saved.
     * @param model Adds attribute to html page.
     * @return Html page.
     * @author jwm22
     */
    @RequestMapping("/upload")
    public String uploadFile(Model model){
        model.addAttribute("paperdata", new UserFile());
        return "files/upload";
    }


    /***
     * First checks if the file type is of type pdf if so then it performs the
     * upload feature in the service class and gives a success return statement if not
     * then returns an error that the file is not type pdf. Also handles files that are bigger than
     * 10MB.
     * @param file The file the user has uploaded.
     * @param redirectAttributes Gives the messages to the upload page.
     * @param principal Get current user.
     * @param paperdata @ModelAttribute to get data such as title, authors and description.
     * @return The html page.
     * @throws IOException
     * @author jwm22
     */
    @PostMapping("/uploadFile")
    public String fileUpload(@RequestParam("file")MultipartFile file, RedirectAttributes redirectAttributes, Principal principal, @ModelAttribute UserFile paperdata) throws IOException {
        try {
            //Checks if the file is type pdf, if not then return the page
            if(file.getContentType().equals("application/pdf")) {
                redirectAttributes.addFlashAttribute("result", "Upload was Successful!");
                MyUser user = userRepository.findByEmail(principal.getName());
                service.Upload(file, user, paperdata);
            }else{
                redirectAttributes.addFlashAttribute("result", "Please Upload a file that is PDF format.");
            }
            return "redirect:/upload";
            //If no file is submitted then the exception will catch this and redirect back to the upload page
        }catch (Exception e){
            return "redirect:/upload";
        }
    }

    /***
     * Handles the problem of a user uploading a file bigger than 10MB.
     * @param redirectAttributes Adds to the html page.
     * @return Redirects to upload page.
     * @author jwm22
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleFileUploadError(RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("result", "File size is bigger than 10MB.");
        return "redirect:/upload";
    }

}
