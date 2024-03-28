package com.intuit.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.model.User;
import com.intuit.mq.consumer.JmsConsumer;
import com.intuit.mq.producer.JmsProducer;
import com.intuit.repository.JMSMessageRepository;
import com.intuit.repository.UserRepository;

@WebMvcTest(UserController.class)
public class UserControllerTest {
  @MockBean
  private UserRepository userRepository;

  @MockBean
  private JMSMessageRepository jmsMessageRepository;

  @MockBean
  private JmsProducer jmsProducer;

  @MockBean
  private JmsConsumer jmsConsumer;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldCreateUser() throws Exception {
    User user = getNewUser();

    mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(user)))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  void shouldReturnListOfUsers() throws Exception {
    List<User> users = new ArrayList<>(
        Arrays.asList(getNewUser()));

    when(userRepository.findAll()).thenReturn(users);
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(users.size()))
        .andDo(print());
  }

  @Test
  void shouldReturnListOfUsersWithFilter() throws Exception {
    List<User> users = new ArrayList<>(
        Arrays.asList(getNewUser()));

    String firstName = "TestFirstName";
    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    paramsMap.add("firstname", firstName);

    when(userRepository.findByFirstNameContainingIgnoreCase(firstName)).thenReturn(users);
    mockMvc.perform(get("/api/users/filter").params(paramsMap))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(users.size()))
        .andDo(print());
  }
  
  @Test
  void shouldReturnNoContentWhenFilter() throws Exception {
    String firstName = "Failure";
    MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
    paramsMap.add("firstname", firstName);
    
    List<User> users = Collections.emptyList();

    when(userRepository.findByFirstNameContainingIgnoreCase(firstName)).thenReturn(users);
    mockMvc.perform(get("/api/users/filter").params(paramsMap))
        .andExpect(status().isNoContent())
        .andDo(print());
  }

  @Test
  void shouldUpdateUser() throws Exception {
    long id = 1L;

    User user = getNewUser();
    User updateduser = user;
    updateduser.setFirstName("UpdatedFirstName");
    updateduser.setLastName("UpdatedLastName");
    updateduser.setEmail("UpdatedEmailId");

    when(userRepository.findById(id)).thenReturn(Optional.of(user));
    when(userRepository.save(any(User.class))).thenReturn(updateduser);

    mockMvc.perform(put("/api/users/{id}", id).contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateduser)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value(updateduser.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(updateduser.getLastName()))
        .andExpect(jsonPath("$.email").value(updateduser.getEmail()))
        .andDo(print());
  }
  
  @Test
  void shouldDeleteUser() throws Exception {
    long id = 1L;
    User user = getNewUser();
    
    when(userRepository.findById(id)).thenReturn(Optional.of(user));
    doNothing().when(userRepository).deleteById(id);
    mockMvc.perform(delete("/api/users/{id}", id))
         .andExpect(status().isNoContent())
         .andDo(print());
  }
  
  @Test
  void shouldDeleteAllUsers() throws Exception {
    doNothing().when(userRepository).deleteAll();
    mockMvc.perform(delete("/api/users"))
         .andExpect(status().isNoContent())
         .andDo(print());
  }
  
  private User getNewUser() {
	    User user = new User();
	    user.setId(1L);
	    user.setFirstName("TestFirstName");
	    user.setLastName("TestLastName");
	    user.setEmail("testuser@t.com");
	    user.setStatus("Active");
	    user.setPhoneNumber("248-736-1872");
	    user.setDob(LocalDate.now());
	    user.setAddress("488 Woodview ct, Rochester");
	    return user;
  }
}