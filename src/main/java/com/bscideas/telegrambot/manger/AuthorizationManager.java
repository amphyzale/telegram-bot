package com.bscideas.telegrambot.manger;

public interface AuthorizationManager {
    boolean hasUserAuthority(String telegramId);
    boolean hasMentorAuthority(String telegramId);

    boolean hasInternAuthority(String telegramId);
}
