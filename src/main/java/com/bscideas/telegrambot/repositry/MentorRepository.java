package com.bscideas.telegrambot.repositry;

import com.bscideas.telegrambot.model.Mentor;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MentorRepository extends MongoRepository<Mentor, String> {
}
