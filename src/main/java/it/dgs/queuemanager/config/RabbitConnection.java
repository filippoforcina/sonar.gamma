package it.dgs.queuemanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rabbitmq.client.ConnectionFactory;

@Configuration
public class RabbitConnection {

	@Value("${spring.rabbitmq.host}")
	private String rabbitmq_host;
	@Value("${spring.rabbitmq.username}")
	private String rabbitmq_username;
	@Value("${spring.rabbitmq.password}")
	private String rabbitmq_password;

	@Bean
	ConnectionFactory clientConnectionFactory() {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(rabbitmq_host);
		connectionFactory.setUsername(rabbitmq_username);
		connectionFactory.setPassword(rabbitmq_password);
		return connectionFactory;
	}

}
