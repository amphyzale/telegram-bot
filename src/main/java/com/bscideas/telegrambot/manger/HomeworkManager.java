package com.bscideas.telegrambot.manger;

import com.bscideas.telegrambot.model.Homework;
import com.bscideas.telegrambot.model.HomeworkStatus;

import java.util.List;

public interface HomeworkManager {

    Homework createHomework(String telegramId, String url);
    List<Homework> getHomeworks(String telegramId, HomeworkStatus status);
    void toCheckHomework(String mentorId, String homeworkId);
    void toResolveHomework(String mentorId, String homeworkId, String status);
    Homework correctHomework(String telegramId, String homeworkId);

}
