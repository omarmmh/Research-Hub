package com.webapp.researchhub.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.researchhub.domain.Forum.ForumThread;
import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.UserFile;
import com.webapp.researchhub.repository.FileRepository;
import com.webapp.researchhub.repository.Forum.ForumThreadRepository;
import com.webapp.researchhub.repository.UserRepository;
import com.webapp.researchhub.service.ExternalApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping("/search")
public class SearchController {
    @Autowired
    UserRepository userRepos;

    @Autowired
    FileRepository fileRepos;

    @Autowired
    ForumThreadRepository threadRepos;
    @Autowired
    private ExternalApiService api;

    @GetMapping("/")
    public String search(Model model){
        List start= new ArrayList<>();
        model.addAttribute("result", start);
        return "searchBar/search";
    }


    /**
     *
     * The Controller with return the 4 search results of possibilities:
     * <pre>
     * - apiResults
     *      results from google scholar
     * - userResults
     *      results of users in the system
     * - fileResults
     *      files uploaded to the system
     * - threadResults
     *      threads posted to the forum
     *
     * the Json return will always be lists, the layouts will be:
     *
     * apiResults =
     *
     *       {[
     *         "position": int,
     *         "title": String,
     *         "result_id": String,
     *         "link": String,
     *         "snippet": String,
     *         "publication_info": {
     *           "summary": String
     *         },
     *         "inline_links": {
     *           "serpapi_cite_link": String,
     *           "cited_by": {
     *             "total": int,
     *             "link": String,
     *             "cites_id": String,
     *             "serpapi_scholar_link": String
     *           },
     *           "related_pages_link": String,
     *           "serpapi_related_pages_link": String,
     *           "versions": {
     *             "total": int,
     *             "link": String,
     *             "cluster_id": String,
     *             "serpapi_scholar_link": String
     *           },
     *           "cached_page_link": String
     *         }
     *       ]}
     *
     *
     *   userResults =
     *        {
     *         [
     *          "name": String,
     *          "username": String,
     *          "email": String,
     *          "link": String
     *         ]
     *        }
     *
     *   fileResults =
     *       {
     *        [
     *         "title": String,
     *         "description": String,
     *         "uploadDate": String,
     *         "Authors": [
     *          {"name": String},
     *          {"name:" String},
     *          ...
     *         ]
     *        ]
     *       }
     *   threadResults =
     *      {
     *       [
     *        "title": String,
     *        "cat": String,
     *        "link": String,
     *        "userLink": String,
     *        "snippet": String,
     *        "user": String,
     *       ]
     *      }
     *
     * </pre>
     *
     * @param search : the search query
     * @param model
     * @return a list of objects as a JSON

     *
     *
     * @throws JsonProcessingException
     */
    @RequestMapping("/")
    public String userSearch(@RequestParam(value="search") String search,
                             @RequestParam(value="select") String select,
                             Principal principal,
                             Model model) throws JsonProcessingException {
        model.addAttribute("search", search);
        model.addAttribute("select", select);

        // External Papers Results
        Object apiResult = api.callApi(search);
        model.addAttribute("apiSearch", apiResult);


        // User Results
        String toJson = "{\"results\":[ ";
        // Read all users
        ArrayList<MyUser> users = userRepos.findAllByFirstNameContains(search);
        users.addAll(userRepos.findAllBySurnameContains(search));
        users.addAll(userRepos.findAllByUsernameContains(search));
        if(!users.isEmpty()) {
            // removes duplications
            users = new ArrayList<MyUser>(new LinkedHashSet<MyUser>(users));


            // structure as a JSON
            for(MyUser usr : users) {
                // Handels the email privacy.
                switch(usr.getAccount().getDisplayEmail()) {
                    case "none":
                        usr.setEmail(null);
                        break;
                    case "loggedin":
                        if (principal == null) {
                            usr.setEmail(null);
                            break;
                        }
                }
                // Handels the full name privacy.
                switch(usr.getAccount().getDisplayName()) {
                    case "none":
                        usr.setFirstName("");
                        usr.setSurname("");
                        break;
                    case "loggedin":
                        if (principal == null) {
                            usr.setFirstName("");
                            usr.setSurname("");
                            break;
                        }
                }


                toJson += "{\"username\":\"" + usr.getUsername() + "\","
                        + "\"link\":\"" + "/profile/" + usr.getUsername() + "\",";
                if(usr.getFirstName() != null && usr.getSurname() != null) {
                    toJson += "\"name\":\"" + usr.getFullName() + "\",";
                }
                if(usr.getEmail() != null) {
                    toJson += "\"email\":\"" + usr.getEmail() + "\",";
                }
                if(usr.getProfilePic() != null) {
                    toJson += "\"profilepic\":\"" + String.format("data:image/png;base64,%s", Base64.getEncoder().encodeToString(usr.getProfilePic().getData())) + "\",";
                }
                toJson = toJson.substring(0,toJson.length()-1);
                toJson += "},";
            }
            // remove last comma for end of list
            toJson = toJson.substring(0,toJson.length()-1);
            toJson += "]}";


            model.addAttribute("userSearch", convertToJson(toJson));
        }


        // Internal Papers Results
        toJson = "{\"results\":[ ";
        // read all relevant papers
        ArrayList<UserFile> files = fileRepos.findAllByTitleContains(search);
        if(!files.isEmpty()) {
            // structure as a JSON
            for(UserFile file : files) {
                Boolean showFile= true;

                // Handels the research papers privacy.
                switch(file.getUser().getAccount().getDisplayPapers()) {
                    case "none":
                        showFile= false;
                        break;
                    case "loggedin":
                        if (principal == null) {
                            showFile= false;
                            break;
                        }
                }

                if(showFile) {
                    toJson += "{\"title\":\"" + file.getTitle() + "\","
                            + "\"snippet\":\"" + file.getDescription() + "\","
                            + "\"uploader\":\"" + file.getUser().getUsername() + "\","
                            + "\"date\":\"" + file.getDateUploaded() + "\","
                            + "\"viewLink\":\"/paper/" + file.getId() + "\","
                            + "\"downloadLink\":\"/download/" + file.getId() + "\","
                            + "\"authors\": [";
                    for (String author : file.getAuthors()) {
                        toJson += "{\"name\":\"" + author + "\"},";

                    }

                    toJson = toJson.substring(0, toJson.length() - 1);

                    toJson += "]},";
                }
            }
            toJson = toJson.substring(0,toJson.length()-1);
            toJson += "]}";

            if(!convertToJson(toJson).toString().equals("[]")) {
                model.addAttribute("fileSearch", convertToJson(toJson));
            }
        }


        // Forum Thread Results
        toJson = "{\"results\":[ ";
        ArrayList<ForumThread> threads = threadRepos.findAllByTitleContains(search);

        if(!threads.isEmpty()) {
            for(ForumThread thread : threads) {
                Boolean showThread= true;

                // Handels the forum activity privacy.
                switch(thread.getUser().getAccount().getDisplayForumActivity()) {
                    case "none":
                        showThread= false;
                        break;
                    case "loggedin":
                        if (principal == null) {
                            showThread= false;
                            break;
                        }
                }

                if (showThread) {
                    SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MM-yyyy");
                    toJson += "{\"title\":\"" + thread.getTitle() + "\","
                            + "\"cat\":\"" + thread.getCategory().getTitle() + "\","
                            + "\"date\":\"" + simpleDateFormat.format(thread.getDateCreated()) + "\","
                            + "\"link\":\"/forum/" + thread.getCategory().getId() + "/thread/" + thread.getId() + "\","
                            + "\"userLink\":\"/profile/" + thread.getUser().getUsername() + "\","
                            + "\"snippet\":\"" + thread.getSnippet() + "\","
                            + "\"user\":\"" + thread.getUser().getUsername() + "\"},";
                }
            }
            toJson = toJson.substring(0,toJson.length()-1);
            toJson += "]}";

            if(!convertToJson(toJson).toString().equals("[]")) {
                model.addAttribute("threadSearch", convertToJson(toJson));
            }
        }
        return "searchBar/search";
    }

    /**
     * Converts a string set in a JSON layout and returns an object which will function as the JSON script.
     *
     * @param toJson : the String to convert to a JSON
     * @return the object to be used as a JSON
     * @throws JsonProcessingException
     */
    private Object convertToJson(String toJson) throws JsonProcessingException {
        // convert into an object that can be used in the frontend
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        Map<String, Object> json = objMapper.readValue(toJson, new TypeReference<Map<String, Object>>() {});
        return json.get("results");
    }
}
