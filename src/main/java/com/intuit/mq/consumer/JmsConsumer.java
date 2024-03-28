package com.intuit.mq.consumer;

import java.time.LocalDateTime;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.model.JMSMessage;
import com.intuit.repository.JMSMessageRepository;

@Component
public class JmsConsumer {

	@Value("${spring.activemq.queue}")
	String queue;

	@Autowired
	JMSMessageRepository jmsMessageRepository;

	@Autowired
	ActiveMQConnectionFactory connectionFactory;

	@JmsListener(destination = "myQueue")
	@SendTo("myQueue2")
	private void receiveMessage(final Message jsonMessage)
			throws JMSException, JsonMappingException, JsonProcessingException {
		String messageData = null;
		System.out.println("Received message " + jsonMessage);
		if (jsonMessage instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) jsonMessage;
			messageData = textMessage.getText();
			System.out.println("messageData:" + messageData);

			JMSMessage jmsMessage = new ObjectMapper().readValue(messageData, JMSMessage.class);
			jmsMessage.setCreatedTime(LocalDateTime.now());
			jmsMessageRepository.save(jmsMessage);

		}
	}
}