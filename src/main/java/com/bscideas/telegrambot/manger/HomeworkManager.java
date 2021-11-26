package com.bscideas.telegrambot.manger;

import com.bscideas.telegrambot.model.Homework;
import com.bscideas.telegrambot.model.HomeworkStatus;

import java.util.List;
import java.util.Optional;

public interface HomeworkManager {

    Homework createHomework(String telegramId, String url);
    Optional<Homework> getHomework(String homeworkId);
    List<Homework> getHomeworks(String telegramId, HomeworkStatus status);
    void toCheckHomework(String mentorId, String homeworkId);
    void toResolveHomework(String mentorId, String homeworkId, HomeworkStatus status);
    Homework correctHomework(String telegramId, String homeworkId);

}
