package com.webapp.researchhub.controller;

import com.webapp.researchhub.domain.UserFile;
import com.webapp.researchhub.domain.calendar.Event;
import com.webapp.researchhub.repository.Calendar.EventRepository;
import com.webapp.researchhub.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/site")
public class SiteController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    FileRepository fileRepository;

    @RequestMapping("/")
    public String index(Model model) {
        Date date = new Date();
        date.setTime(0);
        List<UserFile> latestPapers = (List<UserFile>) fileRepository.findTop6ByDateUploadedAfterOrderByDateUploadedAsc(date);
        List<Event> eventList = eventRepository.findTop6ByStartDateAfterOrderByStartDateAsc(date);
        model.addAttribute("papers", latestPapers);
        model.addAttribute("events", eventList);
        return "site/index";
    }

    @RequestMapping("/about")     //navigation to about us
    public String about() {
        return "site/about";
    }


}
