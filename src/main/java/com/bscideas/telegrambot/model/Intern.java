package com.bscideas.telegrambot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "intern")
public class Intern {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "patronymic")
    private String patronymic;

    @Column(name = "fio")
    private String fio;

    @Column(name = "telegram_id")
    private String telegramId;

    @ManyToMany
    @JoinTable(name = "intern_internship",
            joinColumns = {@JoinColumn(name = "intern_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "internship_id", referencedColumnName = "id")})
    private Set<Internship> internship;

    @OneToMany(mappedBy = "intern", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Homework> homework;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private InternStatus status;

}
