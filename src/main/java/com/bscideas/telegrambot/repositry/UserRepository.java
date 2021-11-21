package com.bscideas.telegrambot.repositry;

import com.bscideas.telegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User findByTelegramId(String telegramId);
}
