package com.bscideas.telegrambot.manger.impl;

import com.bscideas.telegrambot.manger.HomeworkManager;
import com.bscideas.telegrambot.model.Homework;
import com.bscideas.telegrambot.model.HomeworkStatus;
import com.bscideas.telegrambot.model.Intern;
import com.bscideas.telegrambot.model.User;
import com.bscideas.telegrambot.repositry.HomeworkRepository;
import com.bscideas.telegrambot.repositry.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HomeworkManagerImpl implements HomeworkManager {

    private final HomeworkRepository homeworkRepository;
    private final UserRepository userRepository;

    @Override
    public Homework createHomework(String telegramId, String url) {
        final Homework homework = new Homework();
        homework.setHomeworkUrl(url);
        homework.setStatus(HomeworkStatus.WAITING);
        final User user = userRepository.findByTelegramId(telegramId);
        homework.setIntern((Intern) user);
        return homeworkRepository.save(homework);

    }

    @Override
    public List<Homework> getHomeworks(String telegramId, HomeworkStatus status) {
        //TODO may by add authenticate MENTOR or INTERN check? And Internship filter
        return homeworkRepository.findAllByStatus(status);
    }

    @Override
    public void toCheckHomework(String mentorId, String homeworkId) {

    }

    @Override
    public void toResolveHomework(String mentorId, String homeworkId, String status) {

    }

    @Override
    public Homework correctHomework(String telegramId, String homeworkId) {
        return null;
    }
}
