package ru.practicum.ewm.request.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participation_requests")
@NoArgsConstructor
@Getter
@Setter
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_requests_id")
    private Long id; // Идентификатор заявки

    @Column(name = "participation_requests_created", nullable = false)
    private LocalDateTime created; // Дата и время создания заявки

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_requests_event_id", nullable = false)
    private Event event; // Идентификатор события

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_requests_requester_id", nullable = false)
    private User requester; // Идентификатор пользователя, отправившего заявку

    @Enumerated(EnumType.STRING)
    @Column(name = "participation_requests_status", nullable = false)
    private StateRequest status; // Статус заявки

}