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
import java.util.concurrent.TimeoutException;

public class TutorFor {

	private static Logger log = LoggerFactory.getLogger(TutorFor.class);

	private final static String SpringRabbitmqHost = "localhost";
	private final static String SpringRabbitmqUsername = "guest";
	private final static String SpringRabbitmqPassword = "guest";
	private final static String SpringRabbitmqVirtualHost = "code";

	private static final String ExchangeName = "tutor.for.exd";
	private static final String ExchangeType = "direct";

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

	public void receive(String routingKey) throws IOException, TimeoutException, InterruptedException {
		log.info("receive");
		ConnectionFactory connectionFactory = getConnectionFactory();
		Connection connection = connectionFactory.newConnection();
		Channel channel = connection.createChannel();

		String queueName = channel.queueDeclare().getQueue();
		log.info("create queue: {}", queueName);
		channel.queueBind(queueName, ExchangeName, routingKey);

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			String message = new String(delivery.getBody(), "UTF-8");
			log.info("received: {}", message);
		};
		channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
		});
	}

	public void send(StockDTO dto) throws IOException, TimeoutException {
		log.info("send");
		ConnectionFactory connectionFactory = getConnectionFactory();
		try (Connection connection = connectionFactory.newConnection();
			 Channel channel = connection.createChannel()) {

			channel.basicPublish(ExchangeName, dto.getRoute(), null, dto.getMessage().getBytes());
			log.info("send on exchange: {}, routekey: {}, message: {}", ExchangeName, dto.getRoute(), dto.getMessage());

		}
	}

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		log.info("START");
		TutorFor main = new TutorFor();

		main.init();

		main.receive(Router.SMALL.name());
		main.receive(Router.MEDIUM.name());
		main.receive(Router.LARGE.name());

		main.send(new StockDTO(Router.SMALL.name(), "Small 01"));
		main.send(new StockDTO(Router.MEDIUM.name(), "Medium 02"));
		main.send(new StockDTO(Router.LARGE.name(), "Large 03"));
		main.send(new StockDTO(Router.CHECK.name(), "Check 04"));
		main.send(new StockDTO(Router.ERROR.name(), "Error 05"));

		log.info("END");
	}

}
