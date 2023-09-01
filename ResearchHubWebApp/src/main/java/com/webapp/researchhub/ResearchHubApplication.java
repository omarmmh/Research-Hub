package com.webapp.researchhub;

import com.webapp.researchhub.domain.Account;
import com.webapp.researchhub.domain.Forum.ForumCategory;
import com.webapp.researchhub.domain.Forum.ForumComment;
import com.webapp.researchhub.domain.Forum.ForumThread;
import com.webapp.researchhub.domain.MyUser;
import com.webapp.researchhub.domain.Role;
import com.webapp.researchhub.domain.calendar.Event;
import com.webapp.researchhub.repository.AccountRepository;
import com.webapp.researchhub.repository.Calendar.EventRepository;
import com.webapp.researchhub.repository.Forum.ForumCategoryRepository;
import com.webapp.researchhub.repository.Forum.ForumCommentRepository;
import com.webapp.researchhub.repository.Forum.ForumThreadRepository;
import com.webapp.researchhub.repository.ProfilePicRepository;
import com.webapp.researchhub.repository.RoleRepository;
import com.webapp.researchhub.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.Date;

@SpringBootApplication
public class ResearchHubApplication {

    private static UserRepository userRepo;
    private static RoleRepository roleRepo;

    private static AccountRepository accountRepo;
    private static ProfilePicRepository pfpRepo;
    private static PasswordEncoder pe;

    private static ForumCategoryRepository forumCategoryRepo;
    private static ForumThreadRepository forumThreadRepo;
    private static ForumCommentRepository forumCommentRepo;

    private static EventRepository eventRepository;


    public ResearchHubApplication(PasswordEncoder encoder,
                                  UserRepository userRepository,
                                  RoleRepository roleRepository,
                                  AccountRepository accountRepository,
                                  ProfilePicRepository profilePicRepository,
                                  ForumCategoryRepository forumCategoryRepository,
                                  ForumThreadRepository forumThreadRepository,
                                  ForumCommentRepository forumCommentRepository,
                                  EventRepository eventRepository) {
        pe = encoder;
        userRepo = userRepository;
        roleRepo = roleRepository;

        accountRepo = accountRepository;
        pfpRepo = profilePicRepository;

        forumCategoryRepo = forumCategoryRepository;
        forumThreadRepo = forumThreadRepository;
        forumCommentRepo = forumCommentRepository;
        this.eventRepository = eventRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ResearchHubApplication.class, args);
        /*
         * Driver Code
         * Create Roles and Users to work with during development.
         * */

        Role userRole = new Role("USER");
        Role adminRole = new Role("ADMIN");
        roleRepo.save(userRole);
        roleRepo.save(adminRole);


        // Admin user
        MyUser user = new MyUser();
        user.setUsername("mmyers");
        user.setFirstName("Michael");
        user.setSurname("Myers");
        user.setEmail("mike@myers.com");
        user.setPassword(pe.encode("password"));
        user.setBio("Hi, I am Michael Myers.");
        user.setCountry("United Kingdom");
        user.setResearchFacility("The Magnus Institute");
        user.getRoles().add(roleRepo.findByName("USER"));
        user.getRoles().add(roleRepo.findByName("ADMIN"));
        Account account = new Account();
        account.setDisplayForumActivity("all");
        user.setAccount(account);
        accountRepo.save(account);
        userRepo.save(user);

        // Standard user
        user = new MyUser();
        user.setUsername("jcarr");
        user.setFirstName("James");
        user.setSurname("Carr");
        user.setEmail("jc869@student.le.ac.uk");
        user.setPassword(pe.encode("password"));
        user.getRoles().add(roleRepo.findByName("USER"));
        account = new Account();
        user.setAccount(account);
        accountRepo.save(account);
        userRepo.save(user);


        // Standard user
        user = new MyUser();
        user.setUsername("tstark");
        user.setFirstName("Tony");
        user.setSurname("Stark");
        user.setEmail("tony@mcu.com");
        user.setPassword(pe.encode("password"));
        user.getRoles().add(roleRepo.findByName("USER"));
        account = new Account();
        account.setDisplayEmail("all");
        user.setAccount(account);
        accountRepo.save(account);
        userRepo.save(user);

        // Standard user
        user = new MyUser();
        user.setUsername("kfrog");
        user.setFirstName("Kermit");
        user.setSurname("The Frog");
        user.setEmail("kermit@muppet.com");
        user.setPassword(pe.encode("password"));
        user.getRoles().add(roleRepo.findByName("USER"));
        account = new Account();
        user.setAccount(account);
        accountRepo.save(account);
        userRepo.save(user);


        // Standard user
        user = new MyUser();
        user.setUsername("arose");
        user.setFirstName("Amy");
        user.setSurname("Rose");
        user.setEmail("amy@hammer.com");
        user.setPassword(pe.encode("password"));
        user.getRoles().add(roleRepo.findByName("USER"));
        account = new Account();
        account.setDisplayEmail("all");
        user.setAccount(account);
        accountRepo.save(account);
        userRepo.save(user);

        // Standard user
        user = new MyUser();
        user.setUsername("hgranger");
        user.setFirstName("Hermione");
        user.setSurname("Granger");
        user.setEmail("granger@hogwarts.uk");
        user.setPassword(pe.encode("password"));
        user.getRoles().add(roleRepo.findByName("USER"));
        account = new Account();
        user.setAccount(account);
        accountRepo.save(account);
        userRepo.save(user);


        // Standard user
        user = new MyUser();
        user.setUsername("norsinov");
        user.setFirstName("Nikola");
        user.setSurname("Orsinov");
        user.setEmail("orsinov@magnus.com");
        user.setPassword(pe.encode("password"));
        user.getRoles().add(roleRepo.findByName("USER"));
        account = new Account();
        account.setDisplayEmail("all");
        user.setAccount(account);
        accountRepo.save(account);
        userRepo.save(user);


        /*********** Create some forum data ***********/

        /*********** COMMENTS ***********/
        ForumComment c = new ForumComment();
        c.setUser(userRepo.findByEmail("tony@mcu.com"));
        c.setBody("This is an example comment to a post.");

        ForumComment c1 = new ForumComment();
        c1.setUser(userRepo.findByEmail("jc869@student.le.ac.uk"));
        c1.setBody("This is another example comment to the same post.");

        /*********** THREADS ***********/
        ForumThread t = new ForumThread();
        t.setTitle("Where to start with Janus?");
        t.setBody("General advice and tips on how to get started with Janus");
        t.setUser(userRepo.findByEmail("jc869@student.le.ac.uk"));
        c.setThread(t);
        c1.setThread(t);
        t.getCommentList().add(c);
        t.getCommentList().add(c1);

        ForumThread t1 = new ForumThread();
        t1.setTitle("Best reversible computation programming language?");
        t1.setBody("Suggestions on what language is best?");
        t1.setUser(userRepo.findByEmail("mike@myers.com"));


        /*********** CATEGORIES ***********/
        ForumCategory cat = new ForumCategory();
        cat.setTitle("Janus");
        cat.setBody("Technical discussions related to Janus");
        cat.setUser(userRepo.findByEmail("mike@myers.com"));
        t.setCategory(cat);
        t1.setCategory(cat);
        cat.getForumThreadList().add(t);
        cat.getForumThreadList().add(t1);
        forumCategoryRepo.save(cat);

        cat = new ForumCategory();
        cat.setTitle("General");
        cat.setBody("General reversible computation discussion");
        cat.setUser(userRepo.findByEmail("mike@myers.com"));
        forumCategoryRepo.save(cat);

        cat = new ForumCategory();
        cat.setTitle("Reversible and quantum circuits");
        cat.setBody("Reversible and quantum circuits discussion");
        cat.setUser(userRepo.findByEmail("mike@myers.com"));
        forumCategoryRepo.save(cat);

        /*********** Events ***********/
        user= userRepo.findByEmail("tony@mcu.com");

        Date today = new Date();
        Date endDate = new Date();
        endDate = Date.from(endDate.toInstant().plus(Duration.ofHours(2)));

        Date tommorow = new Date(today.getTime() + (1000 * 60 * 60 * 24));
        Date endDateTomorrow = Date.from(tommorow.toInstant().plus(Duration.ofHours(2)));

        Event event = new Event("Reversible Computation Conference", " Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ac tortor dignissim convallis aenean et tortor. In hendrerit gravida rutrum quisque non tellus. Diam ut venenatis tellus in metus. Sagittis aliquam malesuada bibendum arcu vitae. Mauris augue neque gravida in fermentum. Eget duis at tellus at urna condimentum. Odio pellentesque diam volutpat commodo sed egestas egestas. Arcu dictum varius duis at consectetur lorem. Condimentum lacinia quis vel eros donec ac. Sapien faucibus et molestie ac feugiat sed lectus. Odio euismod lacinia at quis risus sed vulputate odio ut. Pellentesque nec nam aliquam sem. Sit amet porttitor eget dolor morbi non arcu risus. Lobortis feugiat vivamus at augue eget arcu. Diam in arcu cursus euismod quis. ", new Date(), endDate, false, "Charles Wilson, University of Leicester", user);
        event.setDateModified(new Date());
        event = eventRepository.save(event);

        Event event1 = new Event("Online Conference", "Hosted from the University of Leicester", new Date(), endDate, true, "https://teams.microsoft.com", user);
        event1.setDateModified(new Date());
        event1 = eventRepository.save(event1);

        Event event2 = new Event("Janus Programming Conference", "Everything Janus", tommorow, endDateTomorrow, false, "charles wilson university of leicester", user);
        event2.setDateModified(new Date());
        event2 = eventRepository.save(event2);
    }
}
