package com.bscideas.telegrambot.manger.impl;

import com.bscideas.telegrambot.manger.HomeworkManager;
import com.bscideas.telegrambot.model.*;
import com.bscideas.telegrambot.repositry.HomeworkRepository;
import com.bscideas.telegrambot.repositry.InternshipRepository;
import com.bscideas.telegrambot.repositry.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HomeworkManagerImpl implements HomeworkManager {

    private final UserRepository userRepository;
    private final HomeworkRepository homeworkRepository;
    private final InternshipRepository internshipRepository;

    @Override
    public Homework createHomework(String telegramId, String url, String internshipName) {
        final Homework homework = new Homework();
        homework.setHomeworkUrl(url);
        homework.setStatus(HomeworkStatus.WAITING);
        homework.setInternship(internshipRepository.findByName(internshipName));
        final User user = userRepository.findByTelegramId(telegramId);
        homework.setIntern((Intern) user);
        return homeworkRepository.save(homework);

    }

    @Override
    public Optional<Homework> getHomework(Long homeworkId) {
        return homeworkRepository.findById(homeworkId);
    }

    @Override
    public List<Homework> getHomeworksByStatus(String telegramId, HomeworkStatus status) {
        //TODO may by add authenticate MENTOR or INTERN check? And Internship filter
        return homeworkRepository.findAllByStatus(status);
    }

    @Override
    public void toCheckHomework(String mentorId, Long homeworkId) {
        homeworkRepository.findById(homeworkId)
                .ifPresent(h -> {
                    h.setMentor((Mentor) userRepository.findByTelegramId(mentorId));
                    h.setStatus(HomeworkStatus.ON_CHECK);
                    homeworkRepository.save(h);
                });
    }

    @Override
    public void toResolveHomework(String mentorId, Long homeworkId, HomeworkStatus status) {
        homeworkRepository.findById(homeworkId)
                .ifPresent(h -> {
                    h.setStatus(status);
                    homeworkRepository.save(h);
                });
    }

    @Override
    public Homework correctHomework(Long homeworkId) {
        final Optional<Homework> optionalHomework = homeworkRepository.findById(homeworkId);
        if (optionalHomework.isPresent()) {
            final Homework homework = optionalHomework.get();
            homework.setStatus(HomeworkStatus.WAITING);
            return homeworkRepository.save(homework);
        }
        return null;
    }

    @Override
    public List<Homework> getHomeworksByStatusAndUser(HomeworkStatus status, String telegramId) {
        final User user = userRepository.findByTelegramId(telegramId);
        if (user instanceof Intern) {
            return homeworkRepository.findAllByStatusAndIntern(status, (Intern) user);
        }
        return null;
    }
}
