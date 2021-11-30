package com.bscideas.telegrambot.repositry;

import com.bscideas.telegrambot.model.Homework;
import com.bscideas.telegrambot.model.HomeworkStatus;
import com.bscideas.telegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, Long> {

    List<Homework> findAllByStatus(HomeworkStatus status);
    List<Homework> findAllByStatusAndIntern(HomeworkStatus status, User user);

}
