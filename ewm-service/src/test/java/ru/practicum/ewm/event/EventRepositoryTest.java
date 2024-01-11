package ru.practicum.ewm.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.ewm.BaseDataJpaTest;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.StateEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;

class EventRepositoryTest extends BaseDataJpaTest {

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LocationRepository locationRepository;

    private final Event event1 = new Event();
    private final Event event2 = new Event();

    @BeforeEach
    public void addEvents() {
        Category category = new Category();
        category.setName("party");
        User user = new User();
        user.setEmail("a.leshchenko@mail.ru");
        user.setName("anastasiya");
        Location location1 = new Location();
        location1.setLat(23.98f);
        location1.setLon(94.34f);
        location1.setRadius(0f);
        Location location2 = new Location();
        location2.setLat(30.08f);
        location2.setLon(14.39f);
        location2.setRadius(0f);

        event1.setAnnotation("annotation 1");
        event1.setCategory(category);
        event1.setConfirmedRequests(5L);
        event1.setCreatedOn(LocalDateTime.now());
        event1.setDescription("description 1");
        event1.setEventDate(LocalDateTime.now().plusWeeks(1));
        event1.setInitiator(user);
        event1.setLocation(location1);
        event1.setPaid(true);
        event1.setParticipantLimit(34);
        event1.setPublishedOn(LocalDateTime.now().plusHours(3));
        event1.setRequestModeration(true);
        event1.setState(StateEvent.PUBLISHED);
        event1.setTitle("title 1");

        event2.setAnnotation("annotation 2");
        event2.setCategory(category);
        event2.setConfirmedRequests(9L);
        event2.setCreatedOn(LocalDateTime.now());
        event2.setDescription("description 2");
        event2.setEventDate(LocalDateTime.now().plusWeeks(2));
        event2.setInitiator(user);
        event2.setLocation(location2);
        event2.setPaid(true);
        event2.setParticipantLimit(3);
        event2.setPublishedOn(LocalDateTime.now().plusHours(5));
        event2.setRequestModeration(true);
        event2.setState(StateEvent.PUBLISHED);
        event2.setTitle("title 2");

        categoryRepository.save(category);
        userRepository.save(user);
        locationRepository.save(location1);
        locationRepository.save(location2);
        eventRepository.save(event1);
        eventRepository.save(event2);
    }

    @AfterEach
    public void deleteEvents() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        locationRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Test
    void findByInitiatorId() {
    }

    @Test
    void getAllEventsByAdmin() {
    }

    @Test
    void getAllEvents() {
    }

    @Test
    void getAllEventsByLocation() {
    }

}