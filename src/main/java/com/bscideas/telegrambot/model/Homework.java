package com.bscideas.telegrambot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "homework")
public class Homework {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @ManyToOne
    @JoinColumn(name = "intern_id")
    private Intern intern;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private HomeworkStatus status;

    @Column(name = "description")
    private String description;

    @Column(name = "homework_url")
    private String homeworkUrl;

    @ManyToOne
    @JoinColumn(name = "internship_id")
    private Internship internship;

}
