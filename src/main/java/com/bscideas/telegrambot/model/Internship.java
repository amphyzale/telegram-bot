package com.bscideas.telegrambot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "internship")
public class Internship {

	@Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "chatId")
    private String chatId;

    @Column(name = "intern_check_word")
    private String internCheckWord;

    @Column(name = "mentor_check_word")
    private String mentorCheckWord;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private InternshipStatus status;

}
