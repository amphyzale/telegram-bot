package com.bscideas.telegrambot.repositry;

import com.bscideas.telegrambot.model.Intern;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InternRepository extends JpaRepository<Intern, String> {
}
