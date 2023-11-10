package it.dgs.queuemanager.utils;

import java.util.Properties;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

public class RabbitUtil {

	private final static String SpringRabbitmqHost = "spring.rabbitmq.host";
	// private final static String SpringRabbitmqPort = "spring.rabbitmq.port";
	private final static String SpringRabbitmqUsername = "spring.rabbitmq.username";
	private final static String SpringRabbitmqPassword = "spring.rabbitmq.password";
	// private final static String SpringRabbitmqVirtualHost = "spring.rabbitmq.virtualhost";

	private MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	private CachingConnectionFactory connectionFactory(String host, String username, String password) {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host);
		cachingConnectionFactory.setUsername(username);
		cachingConnectionFactory.setPassword(password);
		return cachingConnectionFactory;
	}

	private CachingConnectionFactory connectionFactory(String host, String username, String password, String virtualHost) {
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host);
		cachingConnectionFactory.setUsername(username);
		cachingConnectionFactory.setPassword(password);
		cachingConnectionFactory.setVirtualHost(virtualHost);
		return cachingConnectionFactory;
	}

	private RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}

	private RabbitTemplate getRabbitTemplate(String host, String username, String password) {
		CachingConnectionFactory connFact = connectionFactory(host, username, password);
		RabbitTemplate rabTemp = rabbitTemplate(connFact);
		return rabTemp;
	}

	private RabbitTemplate getRabbitTemplate(String host, String username, String password, String virtualHost) {
		CachingConnectionFactory connFact = connectionFactory(host, username, password, virtualHost);
		RabbitTemplate rabTemp = rabbitTemplate(connFact);
		return rabTemp;
	}

	public RabbitTemplate getRabbitTemplate() {
		Properties appProperties = new FileUtil().getAppProperties();
		RabbitTemplate rabbitTemplate = getRabbitTemplate(appProperties.getProperty(SpringRabbitmqHost)//
				, appProperties.getProperty(SpringRabbitmqUsername)//
				, appProperties.getProperty(SpringRabbitmqPassword)//
		);
		return rabbitTemplate;
	}

	public RabbitTemplate getRabbitTemplateVirtualHost() {
		Properties appProperties = new FileUtil().getAppProperties();
		RabbitTemplate rabbitTemplate = getRabbitTemplate(appProperties.getProperty(SpringRabbitmqHost)//
				, appProperties.getProperty(SpringRabbitmqUsername)//
				, appProperties.getProperty(SpringRabbitmqPassword)//
				, "code"//
		);
		return rabbitTemplate;
	}

}
