package com.bscideas.telegrambot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "mentor")
public class Mentor {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToMany
    @JoinTable(name = "mentor_internship",
            joinColumns = {@JoinColumn(name = "mentor_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "internship_id", referencedColumnName = "id")})
    private Set<Internship> internship;

    @Column(name = "telegram_id")
    private String telegramId;

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "fio")
    private String fio;

}
