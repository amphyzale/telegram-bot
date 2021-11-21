package com.bscideas.telegrambot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@DiscriminatorValue("intern")
public class Intern extends User {

    @ManyToMany
    @JoinTable(name = "intern_internship",
            joinColumns = {@JoinColumn(name = "intern_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "internship_id", referencedColumnName = "id")})
    private Set<Internship> internship;

    @OneToMany(mappedBy = "intern", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Homework> homework;

}
