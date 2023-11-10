package it.dgs.queuemanager.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitConfig {

	@Bean
	MessageConverter converter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	MessageConverter converter(ObjectMapper objectMapper) {
		return new Jackson2JsonMessageConverter(objectMapper);
	}

	@Bean
	SimpleRabbitListenerContainerFactory rabbitListenerManualAckContainerFactory(ConnectionFactory connectionFactory//
	// , StatefulRetryOperationsInterceptor retryInterceptor//
			, ObjectMapper objectMapper) {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(converter(objectMapper));
		factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		return factory;
	}

}
