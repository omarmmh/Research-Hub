package com.webapp.researchhub.repository.Calendar;

import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.calendar.Event;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface EventRepository extends CrudRepository<Event, Long> {
    List<Event> findTop6ByStartDateAfterOrderByStartDateAsc(Date date);

    List<Event> findTop4ByOrganiserAndStartDateAfterOrderByStartDateAsc(MyUser user, Date date);
    List<Event> findAllByOrganiser(MyUser user);
}
