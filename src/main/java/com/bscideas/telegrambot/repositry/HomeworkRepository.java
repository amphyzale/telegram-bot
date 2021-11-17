package com.bscideas.telegrambot.repositry;

import com.bscideas.telegrambot.model.Homework;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HomeworkRepository extends MongoRepository<Homework, String> {
}
