package com.bscideas.telegrambot.manger;

import com.bscideas.telegrambot.model.Homework;

import java.util.List;

public interface HomeworkManager {

    Homework processHomework(String telegramId, String url);
    List<Homework> getHomeworks(String telegramId, String status);
    void toCheckHomework(String mentorId, String homeworkId);
    void toResolveHomework(String mentorId, String homeworkId, String status);
    Homework correctHomework(String telegramId, String homeworkId);

}
