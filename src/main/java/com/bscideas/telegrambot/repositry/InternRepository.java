package com.bscideas.telegrambot.repositry;

import com.bscideas.telegrambot.model.Intern;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InternRepository extends MongoRepository<Intern, String> {
}
