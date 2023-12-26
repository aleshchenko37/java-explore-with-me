package ru.practicum.ewm.location.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "locations", schema = "public")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "locations_id")
    private Long id; // Идентификатор места проведения события

    @Column(name = "locations_lat", nullable = false)
    private Float lat; // Широта

    @Column(name = "locations_lon", nullable = false)
    private Float lon; // Долгота

    @Column(name = "locations_radius", nullable = false)
    private Float radius;

}