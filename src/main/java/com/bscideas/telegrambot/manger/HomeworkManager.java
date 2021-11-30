package com.bscideas.telegrambot.manger;

import com.bscideas.telegrambot.model.Homework;
import com.bscideas.telegrambot.model.HomeworkStatus;

import java.util.List;
import java.util.Optional;

public interface HomeworkManager {

    Homework createHomework(String telegramId, String url, String internshipName);
    Optional<Homework> getHomework(Long homeworkId);
    List<Homework> getHomeworksByStatus(String telegramId, HomeworkStatus status);
    void toCheckHomework(String mentorId, Long homeworkId);
    void toResolveHomework(String mentorId, Long homeworkId, HomeworkStatus status);
    Homework correctHomework(Long homeworkId);
    List<Homework> getHomeworksByStatusAndUser(HomeworkStatus status, String telegramId);

}
