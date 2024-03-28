package com.intuit.mq.producer;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.model.JMSMessage;

@Component
public class JmsProducer {

	@Value("${spring.activemq.queue}")
	String queue;

	@Autowired
	PooledConnectionFactory pooledConnectionFactory;

	public void sendMessage(JMSMessage jmsMessage) throws JMSException, JsonProcessingException {
		// Establish a connection for the producer.
		final Connection producerConnection = pooledConnectionFactory.createConnection();
		producerConnection.start();

		// Create a session.
		final Session producerSession = producerConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Create a queue named "MyQueue".
		final Destination producerDestination = producerSession.createQueue(queue);

		// Create a producer from the session to the queue.
		final MessageProducer producer = producerSession.createProducer(producerDestination);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		// Create a message.
		String jsonObj = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(jmsMessage);
		final TextMessage producerMessage = producerSession.createTextMessage(jsonObj);

		// Send the message.
		producer.send(producerMessage);
		System.out.println("Message sent.");

		// Clean up the producer.
		producer.close();
		producerSession.close();
		producerConnection.close();
	}
}