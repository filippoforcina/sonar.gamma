package it.dgs.queuemanager.tutor;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TutorTreCons {

	private static Logger log = LoggerFactory.getLogger(TutorTreCons.class);

	private final static String SpringRabbitmqHost = "localhost";
	private final static String SpringRabbitmqUsername = "guest";
	private final static String SpringRabbitmqPassword = "guest";
	private final static String SpringRabbitmqVirtualHost = "code";

	private static final String ExchangeName = "tutor.tre.exf";

	private void aspetta(int seconds) throws InterruptedException {
		TimeUnit.SECONDS.sleep(seconds);
	}

	private ConnectionFactory getConnectionFactory() {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(SpringRabbitmqHost);
		connectionFactory.setUsername(SpringRabbitmqUsername);
		connectionFactory.setPassword(SpringRabbitmqPassword);
		connectionFactory.setVirtualHost(SpringRabbitmqVirtualHost);
		return connectionFactory;
	}

	public void receive() throws IOException, TimeoutException, InterruptedException {
		log.info("receive");
		ConnectionFactory connectionFactory = getConnectionFactory();
		try (Connection connection = connectionFactory.newConnection();
			 Channel channel = connection.createChannel()) {

			String queueName = channel.queueDeclare().getQueue();
			log.info("create queue: {}", queueName);
			channel.queueBind(queueName, ExchangeName, "");

			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
				log.info("received: {}", message);
			};
			channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
			});

			log.info("wait");
			aspetta(100);
		}
	}

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		log.info("START");
		TutorTreCons main = new TutorTreCons();
		main.receive();
		log.info("END");
	}

}
