package com.intuit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.intuit.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
  List<User> findByFirstNameContainingIgnoreCase(String firstName);

  List<User> findByLastNameContainingIgnoreCase(String lastName);
  
  List<User> findByEmailContainingIgnoreCase(String email);
  
}
