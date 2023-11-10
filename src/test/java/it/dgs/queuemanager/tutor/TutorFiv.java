package it.dgs.queuemanager.tutor;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import it.dgs.queuemanager.dto.Router;
import it.dgs.queuemanager.dto.StockDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class TutorFiv {

	private static Logger log = LoggerFactory.getLogger(TutorFiv.class);

	private final static String SpringRabbitmqHost = "localhost";
	private final static String SpringRabbitmqUsername = "guest";
	private final static String SpringRabbitmqPassword = "guest";
	private final static String SpringRabbitmqVirtualHost = "code";

	private static final String ExchangeName = "tutor.fiv.ext";
	private static final String ExchangeType = "topic";

	private ConnectionFactory getConnectionFactory() {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(SpringRabbitmqHost);
		connectionFactory.setUsername(SpringRabbitmqUsername);
		connectionFactory.setPassword(SpringRabbitmqPassword);
		connectionFactory.setVirtualHost(SpringRabbitmqVirtualHost);
		return connectionFactory;
	}

	public void init() throws IOException, TimeoutException {
		log.info("init");
		ConnectionFactory connectionFactory = getConnectionFactory();
		try (Connection connection = connectionFactory.newConnection();
			 Channel channel = connection.createChannel()) {

			channel.exchangeDeclare(ExchangeName, ExchangeType);
			log.info("create exchange: {}", ExchangeName);

		}
	}

	public void bindingQueue(List<String> routingKey) throws IOException, TimeoutException, InterruptedException {
		ConnectionFactory connectionFactory = getConnectionFactory();

		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();

		String queueName = channel.queueDeclare().getQueue();
		log.info("create queue: {}", queueName);

		for (String key : routingKey) {

			channel.queueBind(queueName, ExchangeName, key);
			log.info("bindingQueue: '{}' with routingKey '{}'", queueName, key);

			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
				log.info("queue-message: {} - {}", queueName, message);
			};
			channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
			});

		}
	}

	public void send(String routingKey, StockDTO dto) throws IOException, TimeoutException {
		log.info("send");
		ConnectionFactory connectionFactory = getConnectionFactory();
		try (Connection connection = connectionFactory.newConnection();
			 Channel channel = connection.createChannel()) {

			channel.basicPublish(ExchangeName, routingKey, null, dto.getMessage().getBytes());
			log.info("send on exchange: {}, routekey: {}, message: {}", ExchangeName, routingKey, dto.getMessage());

		}
	}

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		log.info("START");
		TutorFiv main = new TutorFiv();

		main.init();

		main.bindingQueue(Arrays.asList("#"));
		main.bindingQueue(Arrays.asList("medium.yellow", "medium.red", "large.*"));
		main.bindingQueue(Arrays.asList("large.red"));

		main.send("small.yellow", new StockDTO(Router.SMALL.name(), "Small 01"));
		main.send("medium.green", new StockDTO(Router.MEDIUM.name(), "Medium 02"));
		main.send("large.green", new StockDTO(Router.LARGE.name(), "Large 03"));
		main.send("medium.red", new StockDTO(Router.CHECK.name(), "Check 04"));
		main.send("large.red", new StockDTO(Router.ERROR.name(), "Error 05"));

		log.info("END");
	}

}
