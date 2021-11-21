package com.bscideas.telegrambot.manger.impl;

import com.bscideas.telegrambot.manger.RegistrationManager;
import com.bscideas.telegrambot.model.*;
import com.bscideas.telegrambot.repositry.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationManagerImpl implements RegistrationManager {

    private final UserRepository userRepository;

    @Override
    public void register(String fio, String checkWord, String telegramId, String username) {
        final UserRole role = resolveRole(checkWord);
        if (role == UserRole.MENTOR) {
            final Mentor mentor = new Mentor();
            mentor.setRole(role);
            mentor.setFio(fio);
            mentor.setUsername(username);
            mentor.setTelegramId(telegramId);
            mentor.setStatus(UserStatus.ACTIVE);
            userRepository.save(mentor);
            return;
        }
        final Intern intern = new Intern();
        intern.setRole(role);
        intern.setFio(fio);
        intern.setUsername(username);
        intern.setTelegramId(telegramId);
        intern.setStatus(UserStatus.ACTIVE);
        userRepository.save(intern);
    }

    private UserRole resolveRole(String checkWord) {
        return checkWord.equals("TODO") ? UserRole.MENTOR : UserRole.INTERN; //TODO
    }
}
