package it.dgs.queuemanager.tutor;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import it.dgs.queuemanager.dto.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TutorTre {

	private static Logger log = LoggerFactory.getLogger(TutorTre.class);

	private final static String SpringRabbitmqHost = "localhost";
	private final static String SpringRabbitmqUsername = "guest";
	private final static String SpringRabbitmqPassword = "guest";
	private final static String SpringRabbitmqVirtualHost = "code";

	private static final String ExchangeName = "tutor.tre.exf";

	private ConnectionFactory getConnectionFactory() {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(SpringRabbitmqHost);
		connectionFactory.setUsername(SpringRabbitmqUsername);
		connectionFactory.setPassword(SpringRabbitmqPassword);
		connectionFactory.setVirtualHost(SpringRabbitmqVirtualHost);
		return connectionFactory;
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

	public void send(String message, String routingKey) throws IOException, TimeoutException {
		log.info("send");
		ConnectionFactory connectionFactory = getConnectionFactory();
		try (Connection connection = connectionFactory.newConnection();
			 Channel channel = connection.createChannel()) {

			channel.exchangeDeclare(ExchangeName, "fanout");
			log.info("create exchange: {}", ExchangeName);
			channel.basicPublish(ExchangeName, routingKey, null, message.getBytes());
			log.info("send on exchange: {}, routekey: {}, message: {}", ExchangeName, routingKey, message);

		}
	}

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		log.info("START");
		TutorTre main = new TutorTre();
		String routingKey = "xyz";
		main.receive(routingKey);
		main.send(Router.CHECK.name(), routingKey);
		main.send(Router.BASKET.name(), routingKey);
		main.send(Router.ERROR.name(), routingKey);
		main.send(Router.LARGE.name(), routingKey);
		log.info("END");
	}

}
