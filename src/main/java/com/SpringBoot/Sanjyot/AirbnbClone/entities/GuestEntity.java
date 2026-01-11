package com.SpringBoot.Sanjyot.AirbnbClone.entities;

import com.SpringBoot.Sanjyot.AirbnbClone.entities.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Table(name = "guest")
public class GuestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Integer age;

    @ManyToMany(mappedBy = "guests")
    @ToString.Exclude
    private Set<BookingEntity> booking;
}
