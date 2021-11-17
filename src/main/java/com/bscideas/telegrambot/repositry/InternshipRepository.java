package com.bscideas.telegrambot.repositry;

import com.bscideas.telegrambot.model.Internship;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InternshipRepository extends MongoRepository<Internship, String> {
}
