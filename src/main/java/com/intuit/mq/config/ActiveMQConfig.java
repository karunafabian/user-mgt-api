package com.intuit.mq.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

@Configuration
public class ActiveMQConfig {

	@Value("${spring.activemq.broker-url}")
	String BROKER_URL;

	@Value("${spring.activemq.user}")
	String BROKER_USERNAME;

	@Value("${spring.activemq.password}")
	String BROKER_PASSWORD;

	@Bean
	public ActiveMQConnectionFactory connectionFactory() {
		// Create a connection factory.
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);

		// Pass the sign-in credentials.
		connectionFactory.setUserName(BROKER_USERNAME);
		connectionFactory.setPassword(BROKER_PASSWORD);
		return connectionFactory;
	}

	@Bean
	public PooledConnectionFactory createPooledConnectionFactory() {
		// Create a pooled connection factory.
		PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
		pooledConnectionFactory.setConnectionFactory(connectionFactory());
		pooledConnectionFactory.setMaxConnections(10);
		return pooledConnectionFactory;
	}

	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory());
		factory.setConcurrency("1-2");
		return factory;
	}

}
