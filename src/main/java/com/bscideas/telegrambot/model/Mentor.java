package com.bscideas.telegrambot.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Mentor {

    @Id
    private String id;
    private Internship internship;
    private String telegramId;
    private String name;
    private String lastName;
    private String patronymic;
    private String fio;
}
