package com.bscideas.telegrambot.manger.impl;

import com.bscideas.telegrambot.manger.AuthorizationManager;
import com.bscideas.telegrambot.model.User;
import com.bscideas.telegrambot.model.UserRole;
import com.bscideas.telegrambot.repositry.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationManagerImpl implements AuthorizationManager {

    private final UserRepository userRepository;

    @Override
    public boolean hasUserAuthority(String telegramId) {
        return telegramId != null && userRepository.findByTelegramId(telegramId) != null;
    }

    @Override
    public boolean hasMentorAuthority(String telegramId) {
        if (telegramId == null) {
            return false;
        }
        final User user = userRepository.findByTelegramId(telegramId);
        return user != null && user.getRole() == UserRole.MENTOR;
    }

    @Override
    public boolean hasInternAuthority(String telegramId) {
        if (telegramId == null) {
            return false;
        }
        final User user = userRepository.findByTelegramId(telegramId);
        return user != null && user.getRole() == UserRole.INTERN;
    }
}
