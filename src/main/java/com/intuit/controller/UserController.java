package com.intuit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.jms.JMSException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.intuit.model.JMSMessage;
import com.intuit.model.User;
import com.intuit.mq.consumer.JmsConsumer;
import com.intuit.mq.producer.JmsProducer;
import com.intuit.repository.JMSMessageRepository;
import com.intuit.repository.UserRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	JMSMessageRepository jmsMessageRepository;

	@Autowired
	JmsProducer jmsProducer;

	@Autowired
	JmsConsumer jmsConsumer;

	@GetMapping("/users")
	public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) String title) {
		try {
			List<User> users = userRepository.findAll();
			if (users.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(users, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/users/filter")
	public ResponseEntity<List<User>> getUserById(@RequestParam(required = false) String id,
			@RequestParam(required = false) String firstname, @RequestParam(required = false) String lastname,
			@RequestParam(required = false) String email) {
		List<User> users = new ArrayList<User>();

		if (id != null) {
			Optional<User> userData = userRepository.findById(Long.valueOf(id));
			users.add(userData.get());
		} else if (firstname != null) {
			users = userRepository.findByFirstNameContainingIgnoreCase(firstname);
		} else if (lastname != null) {
			users = userRepository.findByLastNameContainingIgnoreCase(lastname);
		} else if (email != null) {
			users = userRepository.findByEmailContainingIgnoreCase(email);
		}

		if (users != null && !users.isEmpty()) {
			return new ResponseEntity<>(users, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	@PostMapping("/users")
	public ResponseEntity<User> createUser(@RequestBody User user) {
		try {
			User _user = userRepository.save(user);
			JMSMessage jmsMessage = new JMSMessage();
			jmsMessage.setMessage("Created user : " + user.getFirstName() + " " + user.getLastName());
			jmsProducer.sendMessage(jmsMessage);
			return new ResponseEntity<>(_user, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/users/{id}")
	public ResponseEntity<User> updateUser(@PathVariable("id") long id, @RequestBody User user)
			throws JsonProcessingException, JMSException {
		try {
			user = userRepository.save(user);
			JMSMessage jmsMessage = new JMSMessage();
			jmsMessage.setMessage("Modified user : " + user.getFirstName() + " " + user.getLastName());
			jmsProducer.sendMessage(jmsMessage);
			return new ResponseEntity<>(user, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<HttpStatus> deleteUser(@PathVariable("id") long id) {
		try {
			User user = userRepository.findById(id).get();
			String userName = user.getFirstName() + " " + user.getLastName();
			userRepository.deleteById(id);
			JMSMessage jmsMessage = new JMSMessage();
			jmsMessage.setMessage("Deleted user : " +userName );
			jmsProducer.sendMessage(jmsMessage);

			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/users")
	public ResponseEntity<HttpStatus> deleteAllUsers() {
		try {
			userRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/users/published")
	public ResponseEntity<List<User>> findByParams(@RequestParam(required = false) String firstName,
			@RequestParam(required = false) String lastName, @RequestParam(required = false) String email) {
		try {
			List<User> users = userRepository.findByFirstNameContainingIgnoreCase(firstName);

			if (users.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(users, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/messages")
	public ResponseEntity<List<JMSMessage>> getAllMessages() {
		try {
			List<JMSMessage> messages = jmsMessageRepository.findAll();
			if (messages.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(messages, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
