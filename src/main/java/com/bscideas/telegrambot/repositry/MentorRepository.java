package com.bscideas.telegrambot.repositry;

import com.bscideas.telegrambot.model.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorRepository extends JpaRepository<Mentor, String> {
}
