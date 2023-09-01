package com.webapp.researchhub.API;

import com.webapp.researchhub.API.ErrorInfo.ErrorInfo;
import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.calendar.DTO.EventDTO;
import com.webapp.researchhub.domain.calendar.EventInterest;
import com.webapp.researchhub.domain.calendar.Event;
import com.webapp.researchhub.repository.Calendar.EventInterestRepository;
import com.webapp.researchhub.repository.Calendar.EventRepository;
import com.webapp.researchhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/event")
public class EventAPI {

    @Autowired
    private EventRepository repo;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventInterestRepository eventInterestRepository;

    @PostMapping("")
    public ResponseEntity<Event> createNewEvent(@RequestBody EventDTO eventDTO)
    {
        System.out.println(eventDTO);
        MyUser user = userRepository.findById(eventDTO.getOrganiserId()).get();
        Event event = new Event(eventDTO.getTitle(), eventDTO.getDescription(), eventDTO.getStartDate(), eventDTO.getEndDate(), eventDTO.getIsVirtual(),eventDTO.getLocation(), user);
        event.setDateModified(eventDTO.getDateModified());
        event = repo.save(event);
        return ResponseEntity.ok(event);
    }

    /**
     *
     * @return JSON body of eventDTO as we only want to get the users ID and Name and if there are no events
     * it returns an error message.
     * @author jwm22
     */
    @GetMapping("")
    public ResponseEntity<?> getUserEvents(){
        List<Event> events = (List<Event>) repo.findAll();
        List<EventDTO> eventsDTO = new ArrayList<>();
        for (Event e: events) {
            EventDTO dto = new EventDTO(e.getId(),e.getTitle(),e.getDescription(),e.getStartDate(),e.getEndDate(),e.isVirtual(),e.getLocation(),e.getOrganiser().getUsername(),e.getOrganiser().getId(),e.getDateModified());
            eventsDTO.add(dto);
        }
        if (events.isEmpty()) {
            String error = "No Events!";
            return new ResponseEntity<ErrorInfo>(new ErrorInfo(error), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<EventDTO>>(eventsDTO, HttpStatus.OK);
    }

    /**
     *
     * @param id Takes the id of an event and returns data of it.
     * @return A json body containing information about the event or if there is no event with the given id it
     * returns an error message.
     * @author jwm22
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserEvent(@PathVariable("id")Long id){
        Optional<Event> event = repo.findById(id);
        if(event.isPresent()) {
            Event e = event.get();
            EventDTO eventDT0 = new EventDTO(e.getId(), e.getTitle(), e.getDescription(), e.getStartDate(), e.getEndDate(), e.isVirtual(),e.getLocation(), e.getOrganiser().getFullName(), e.getOrganiser().getId(),e.getDateModified());
            return new ResponseEntity<EventDTO>(eventDT0, HttpStatus.OK);
        }else{
            String error = "Not Found!";
            return new ResponseEntity<ErrorInfo>(new ErrorInfo(error), HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * @param year Get the events for that given year
     * @return JSON for the events for the given year.
     * @author jwm22
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<?> getEventByYear(@PathVariable("year")int year){
        List<Event> events = (List<Event>) repo.findAll();
        List<EventDTO> eventsDTO = new ArrayList<>();
        if(!events.isEmpty()) {
            for (Event e : events) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(e.getStartDate());
                if (calendar.get(Calendar.YEAR) == year) {
                    EventDTO dto = new EventDTO(e.getId(), e.getTitle(), e.getDescription(), e.getStartDate(), e.getEndDate(), e.isVirtual(),e.getLocation(), e.getOrganiser().getFullName(), e.getOrganiser().getId(), e.getDateModified());
                    eventsDTO.add(dto);
                }else {
                    String error = "No Events this Year!";
                    return new ResponseEntity<ErrorInfo>(new ErrorInfo(error), HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<List<EventDTO>>(eventsDTO, HttpStatus.OK);
        }else{
            String error = "No Events!";
            return new ResponseEntity<ErrorInfo>(new ErrorInfo(error), HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * @param year Get the year for the event.
     * @param month Get the events for that month.
     * @return JSON body with details of the events found for that year and month.
     */
    @GetMapping("/year/{year}/month/{month}")
    public ResponseEntity<?> getEventByMonth(@PathVariable("year")int year,@PathVariable("month")int month){
        List<Event> events = (List<Event>) repo.findAll();
        List<EventDTO> eventsDTO = new ArrayList<>();
        if(!events.isEmpty()) {
            for (Event e:events) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(e.getStartDate());
                if (calendar.get(Calendar.MONTH) == month && calendar.get(Calendar.YEAR) == year){
                    EventDTO dto = new EventDTO(e.getId(),e.getTitle(),e.getDescription(),e.getStartDate(),e.getEndDate(),e.isVirtual(),e.getLocation(),e.getOrganiser().getFullName(),e.getOrganiser().getId(), e.getDateModified());
                    eventsDTO.add(dto);
                }else{
                    String error = "No events for this month!";
                    return new ResponseEntity<ErrorInfo>(new ErrorInfo(error), HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<List<EventDTO>>(eventsDTO, HttpStatus.OK);
        }else if(month > 11 || month < 0){
            String error = "Select Months by using 0-11";
            return new ResponseEntity<ErrorInfo>(new ErrorInfo(error), HttpStatus.NOT_FOUND);
        }else{
            String error = "No events!";
            return new ResponseEntity<ErrorInfo>(new ErrorInfo(error), HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * @param id Takes the id of the event to be edited.
     * @param newEvent Requests a JSON format to pass in to edit event details.
     * @return The updated event.
     * @author jwm22
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> editEvent(@PathVariable("id") Long id, @RequestBody EventDTO newEvent, Principal principal){

        Optional<Event> event = repo.findById(id);
        MyUser user = userRepository.findByEmail(principal.getName());

        if (user.getId() != newEvent.getOrganiserId()){
            String error = "Not the current user logged in.";
            return new ResponseEntity<ErrorInfo>(new ErrorInfo(error), HttpStatus.NOT_ACCEPTABLE);
        } else if(event.isPresent()){
            Event e = event.get();
            e.setDescription(newEvent.getDescription());
            e.setEndDate(newEvent.getEndDate());
            e.setTitle(newEvent.getTitle());
            e.setStartDate(newEvent.getStartDate());
            e.setLocation(newEvent.getLocation());
            e.setDateModified(newEvent.getDateModified());
            e = repo.save(e);
            return new ResponseEntity<EventDTO>(newEvent,HttpStatus.OK);
        }else{
            String error = "Not Found!";
            return new ResponseEntity<ErrorInfo>(new ErrorInfo(error), HttpStatus.NOT_FOUND);
        }
    }

    /**
     *
     * @param id Id of the event we want to delete.
     * @param principal Check if the current user is the user who created the event.
     * @return Deleted Event response or an error if the user trying to delete is not the organiser.
     * @author jwm22
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable("id") Long id, Principal principal){
        Event event = repo.findById(id).get();
        List<EventInterest> eventInterestList = new ArrayList<>();
        if (event.getOrganiser().getEmail().equals(principal.getName())){
            for (EventInterest e:event.getEventInterestList()) {
                eventInterestList.add(e);
            }
            for (EventInterest e:eventInterestList) {
                var user = e.getUser();
                user.getEventInterestList().remove(e);
                userRepository.save(user);
                event.getEventInterestList().remove(e);
                repo.save(event);
            }
            eventInterestRepository.deleteAll(eventInterestList);
            repo.deleteById(id);
            return new ResponseEntity<>("Deleted Event",HttpStatus.OK);
        }else {
            return new ResponseEntity<ErrorInfo>(new ErrorInfo("Not the event organiser for the event."),HttpStatus.NOT_ACCEPTABLE);
        }
    }


    /***
     * User interested will be added to the list of the people going.
     * @param id Id of the event the user is interested in.
     * @param principal Get the user currently logged in.
     * @return
     * @author jwm22
     */
    @PostMapping("/user/{id}")
    public ResponseEntity<?> addUserToEventList(@PathVariable("id") long id, Principal principal) {
        MyUser user = userRepository.findByEmail(principal.getName());
        if (repo.existsById(id)) {
            Event event = repo.findById(id).get();
            EventInterest e = new EventInterest();
            e.setEvent(event);
            e.setUsers(user);
            eventInterestRepository.save(e);
            user.getEventInterestList().add(e);
            userRepository.save(user);
            event.getEventInterestList().add(e);
            repo.save(event);
            return new ResponseEntity<>("Added vote", HttpStatus.OK);
        }else{
            String error = "Not Found!";
            return new ResponseEntity<ErrorInfo>(new ErrorInfo(error), HttpStatus.NOT_FOUND);
        }
    }

    /***
     * Delete the event from users interested list.
     * @param id Id of the event.
     * @param principal Get current user logged in.
     * @return
     * @author jwm22
     */
    @DeleteMapping("/delete/interest/{id}")
    public ResponseEntity<?> removeUserFromInterestedList(@PathVariable("id") long id, Principal principal){
        MyUser user = userRepository.findByEmail(principal.getName());
        if (repo.existsById(id)){
            EventInterest eventInterest = new EventInterest();
            Event event = repo.findById(id).get();
            for (EventInterest e: event.getEventInterestList()) {
                if (e.getUser() == user ){
                    eventInterest = e;
                    break;
                }
            }
            user.getEventInterestList().remove(eventInterest);
            userRepository.save(user);
            event.getEventInterestList().remove(eventInterest);
            repo.save(event);
            eventInterestRepository.deleteById(eventInterest.getId());

            return new ResponseEntity<>("Deleted from interest list!", HttpStatus.OK);
        }else{
            String error = "Not Found!";
            return new ResponseEntity<ErrorInfo>(new ErrorInfo(error), HttpStatus.NOT_FOUND);
        }
    }

    /***
     * Check if user is already interested in an event or not.
     * @param id The Id of the event a user is interested in.
     * @param principal The current user logged in.
     * @return True or false.
     * @author jwm22
     */
    @GetMapping("/{id}/interest")
    public ResponseEntity<?> getEventInterest(@PathVariable("id") long id, Principal principal){
        MyUser user = userRepository.findByEmail(principal.getName());
        boolean interested = false;
        if (repo.existsById(id)){
            Event event = repo.findById(id).get();
            for (EventInterest e: event.getEventInterestList()) {
                if (e.getUser() == user){
                    interested = true;
                }else{
                    interested = false;
                }
            }
            return new ResponseEntity<>(interested, HttpStatus.OK);
        }else{
            String error = "Not Found!";
            return new ResponseEntity<ErrorInfo>(new ErrorInfo(error), HttpStatus.NOT_FOUND);
        }
    }

}
