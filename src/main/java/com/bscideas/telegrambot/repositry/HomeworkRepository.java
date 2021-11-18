package com.bscideas.telegrambot.repositry;

import com.bscideas.telegrambot.model.Homework;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HomeworkRepository extends JpaRepository<Homework, String> {
}
