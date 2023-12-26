package ru.practicum.ewm.event.model;

import lombok.*;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "events_id")
    private Long id; // Идентификатор

    @Column(name = "events_annotation", nullable = false)
    private String annotation; // Краткое описание

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "events_category_id", nullable = false)
    private Category category; // Категория

    @Column(name = "events_confirmed_requests")
    private Long confirmedRequests; // Количество одобренных заявок на участие в данном событии

    @Column(name = "events_created_on")
    private LocalDateTime createdOn; //  Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "events_description", nullable = false)
    private String description; // Полное описание события

    @Column(name = "events_event_date", nullable = false)
    private LocalDateTime eventDate;
    // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "events_initiator_id", nullable = false)
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "events_location_id", nullable = false)
    private Location location; // Широта и долгота места проведения события

    @Column(name = "events_paid", nullable = false)
    private Boolean paid; // Нужно ли оплачивать участие в событии

    @Column(name = "events_participant_limit", nullable = false)
    private Integer participantLimit;
    // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    @Column(name = "events_published_on")
    private LocalDateTime publishedOn;
    // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    @Column(name = "events_request_moderation")
    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие

    @Enumerated(EnumType.STRING)
    @Column(name = "events_state", nullable = false)
    private StateEvent state; // Список состояний жизненного цикла события

    @Column(name = "events_title", nullable = false)
    private String title; // Заголовок

}