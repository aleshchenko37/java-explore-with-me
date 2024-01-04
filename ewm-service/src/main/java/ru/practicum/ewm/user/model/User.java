package ru.practicum.ewm.user.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users") //, schema = "public")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id; // Идентификатор пользователя

    @Column(name = "users_email", nullable = false, unique = true)
    private String email; // Электронный адрес пользователя

    @Column(name = "users_name", nullable = false)
    private String name; // Имя пользователя

}