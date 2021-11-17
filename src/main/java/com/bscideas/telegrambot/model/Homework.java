package com.bscideas.telegrambot.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Homework {

    @Id
    private String id;
    private Intern intern;
    private HomeworkStatus status;
    private String description;
    private String homeworkUrl;
    private Mentor currentMentor;

}
