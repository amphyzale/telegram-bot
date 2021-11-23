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

    @Column(name = "description")
    private String description;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "check_word")
    private String checkWord;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private InternshipStatus status;

}
