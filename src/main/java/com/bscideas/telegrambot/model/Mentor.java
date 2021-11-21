package com.bscideas.telegrambot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@DiscriminatorValue("mentor")
public class Mentor extends User {

    @ManyToMany
    @JoinTable(name = "mentor_internship",
            joinColumns = {@JoinColumn(name = "mentor_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "internship_id", referencedColumnName = "id")})
    private Set<Internship> internship;

    @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Homework> homework;

}
