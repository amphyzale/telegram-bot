package com.bscideas.telegrambot.manger.impl;

import com.bscideas.telegrambot.manger.RegistrationManager;
import com.bscideas.telegrambot.model.Intern;
import com.bscideas.telegrambot.model.Mentor;
import com.bscideas.telegrambot.repositry.InternRepository;
import com.bscideas.telegrambot.repositry.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationManagerImpl implements RegistrationManager {

    private final InternRepository internRepository;
    private final MentorRepository mentorRepository;

    @Override
    public void register(String fio, String checkWord) {
        if (isMentor(checkWord)) {
            final Mentor mentor = new Mentor();
            mentor.setFio(fio);
            mentorRepository.save(mentor);
        } else {
            final Intern intern = new Intern();
            intern.setFio(fio);
            internRepository.save(intern);
        }
    }

    private boolean isMentor(String checkWord) {
        return true; //TODO
    }
}
