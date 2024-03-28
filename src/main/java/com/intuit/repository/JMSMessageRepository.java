package com.intuit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.intuit.model.JMSMessage;

public interface JMSMessageRepository extends JpaRepository<JMSMessage, Long> {
  
}
