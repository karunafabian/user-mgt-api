package com.intuit.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "jms_message")
public class JMSMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(name = "message")
  private String message;
  
  @Column(name = "createdTime")
  private LocalDateTime createdTime;

public long getId() {
	return id;
}

public void setId(long id) {
	this.id = id;
}

public String getMessage() {
	return message;
}

public void setMessage(String message) {
	this.message = message;
}

public LocalDateTime getCreatedTime() {
	return createdTime;
}

public void setCreatedTime(LocalDateTime createdTime) {
	this.createdTime = createdTime;
}
  
  
 
}
