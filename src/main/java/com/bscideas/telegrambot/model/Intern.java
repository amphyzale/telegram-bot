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

    @OneToMany(mappedBy = "intern", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Homework> homework;

}
