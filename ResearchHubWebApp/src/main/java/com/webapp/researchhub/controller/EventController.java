package com.webapp.researchhub.controller;

import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.calendar.Event;
import com.webapp.researchhub.domain.calendar.EventInterest;
import com.webapp.researchhub.repository.Calendar.EventRepository;
import com.webapp.researchhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class EventController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    EventRepository eventRepository;

    @GetMapping("/events")
    public String events(Model model, Principal principal) {
        model.addAttribute("user", userRepository.findByEmail(principal.getName()).getId());
        return "events/index";
    }

    @GetMapping("/events/{id}")
    public String event(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("user", userRepository.findByEmail(principal.getName()).getId());
        model.addAttribute("showEvent", id);
        return "events/index";
    }

    @GetMapping("/myEvents")
    public String event(Model model, Principal principal) {
        MyUser user = userRepository.findByEmail(principal.getName());
        model.addAttribute("events", eventRepository.findAllByOrganiser(user));

        List<Event> userEventsInterest = new ArrayList<>();
        for (EventInterest e: user.getEventInterestList()) {
            userEventsInterest.add(e.getEvent());
        }
        model.addAttribute("eventsInterest", userEventsInterest);

        return "events/viewEvents";
    }
}
