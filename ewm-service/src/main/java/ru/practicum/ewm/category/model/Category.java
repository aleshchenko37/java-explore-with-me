package ru.practicum.ewm.category.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "categories", schema = "public")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categories_id")
    private Long id; // Идентификатор категории

    @Column(name = "categories_name", nullable = false)
    private String name; // Название категории

}