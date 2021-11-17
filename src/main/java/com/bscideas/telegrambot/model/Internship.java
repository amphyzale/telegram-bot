package com.bscideas.telegrambot.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Internship {

	@Id
    private String id;
    private String name;
    private String chatId;
    private String internCheckWord;
    private String mentorCheckWord;
    private InternshipStatus status;

}
