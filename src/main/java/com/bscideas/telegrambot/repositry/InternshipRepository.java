package com.bscideas.telegrambot.repositry;

import com.bscideas.telegrambot.model.Internship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternshipRepository extends JpaRepository<Internship, String> {
}
