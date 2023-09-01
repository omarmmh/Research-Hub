package com.webapp.researchhub.service;

import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.ProfilePic;
import com.webapp.researchhub.domain.UserFile;
import com.webapp.researchhub.repository.FileRepository;
import com.webapp.researchhub.repository.ProfilePicRepository;
import com.webapp.researchhub.repository.UserRepository;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Service
public class FileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ProfilePicRepository profilePicRepository;


    /***
     *This method saves the user's file to the database including its content (bytes[]).
     * @param file The users file.
     * @param user The current user that's logged.
     * @param userFile Takes in the paper data from a controller would be null for other types of file uploads
     * @throws IOException
     * @author jwm22
     */
    public void Upload(MultipartFile file, MyUser user, UserFile userFile) throws IOException {
        //Saving the file uploaded into the database
        if (userFile != null){
            userFile.setName(file.getOriginalFilename());
            userFile.setType(file.getContentType());
            userFile.setData(file.getBytes());
            userFile.setUser(user);
            userFile = fileRepository.save(userFile);
            user.getUserFiles().add(userFile);
            user = userRepository.save(user);
        }else if(file.getContentType().equals("image/png") || file.getContentType().equals("image/jpeg")){
            ProfilePic profilePic = new ProfilePic(file.getName(),file.getContentType(),file.getBytes(),user);
            profilePic = profilePicRepository.save(profilePic);
            user.setProfilePic(profilePic);
            user = userRepository.save(user);
        }

    }

    /***
     * This method uses the HttpServletResponse to send a stream of bytes to users
     * download directory which it gets from the database.
     * @param file The users file.
     * @param response How the server will respond.
     * @throws IOException
     * @author jwm22
     */
    public void download(Optional<UserFile> file, HttpServletResponse response) throws IOException {
        //Get ready for stream of data bytes
        response.setContentType("application/octet-stream");
        //What content to expect
        String headerKey = "Content-Disposition";
        //What the name of the downloaded file will be
        String headerValue = String.format("attachment; filename=\"%s\"", file.get().getName());
        response.setHeader(headerKey, headerValue);
        ServletOutputStream outputStream = response.getOutputStream();
        //Gives the data to the output stream to send to the user
        outputStream.write(file.get().getData());
    }

    /***
     * Deletes a users paper of the website and the database.
     * @param id Id of the file wanting to be deleted.
     */
    public void delete(Long id, Principal principal){
        UserFile file = fileRepository.findById(id).get();
        MyUser user = userRepository.findByEmail(principal.getName());
        user.getUserFiles().remove(file);
        user = userRepository.save(user);
        fileRepository.delete(file);
    }

}