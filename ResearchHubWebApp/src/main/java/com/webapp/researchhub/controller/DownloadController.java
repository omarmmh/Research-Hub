package com.webapp.researchhub.controller;

import com.webapp.researchhub.domain.UserFile;
import com.webapp.researchhub.repository.FileRepository;
import com.webapp.researchhub.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.Optional;

@Controller
public class DownloadController {

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FileService service;


    /***
     * Gets the file from repository using the id given and uses
     * service class to perform the download
     * @param id The id of the paper we are trying to download.
     * @param response The response we are going to give.
     * @throws IOException
     * @author jwm22
     */
    @GetMapping("/download/{id}")
    public void downloadPdf(@PathVariable Long id, HttpServletResponse response) throws IOException {
        //Gets the correct file from the database
        Optional<UserFile> file = fileRepository.findById(id);
        service.download(file,response);
    }

}
