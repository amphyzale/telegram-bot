package com.bscideas.telegrambot.repositry;

import com.bscideas.telegrambot.model.Homework;
import com.bscideas.telegrambot.model.HomeworkStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeworkRepository extends JpaRepository<Homework, String> {

    List<Homework> findAllByStatus(HomeworkStatus status);

}
