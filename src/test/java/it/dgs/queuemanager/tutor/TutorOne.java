package it.dgs.queuemanager.tutor;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import it.dgs.queuemanager.dto.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TutorOne {

	private static Logger log = LoggerFactory.getLogger(TutorOne.class);

	private final static String SpringRabbitmqHost = "localhost";
	private final static String SpringRabbitmqUsername = "guest";
	private final static String SpringRabbitmqPassword = "guest";
	private final static String SpringRabbitmqVirtualHost = "code";

	private static final String QueueName = "tutor.one.quq";

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

	public void sendDTO(String message) throws IOException, TimeoutException {
		log.info("sendDTO");
		ConnectionFactory connectionFactory = getConnectionFactory();
		try (Connection connection = connectionFactory.newConnection();
			 Channel channel = connection.createChannel()) {

			// Map<String, Object> mappa = new HashMap<>();
			// mappa.put("x-queue-type", "quorum");
			// channel.queueDeclare(QueueName, true, false, false, mappa);
			// String message = "Hello World!";
			// channel.basicPublish("", QueueName, null, message.getBytes());
			// log.info("send on queue: {}, message: {}", QueueName, message);
			channel.basicPublish("", QueueName, null, message.getBytes());
			log.info("send on queue: {}, message: {}", QueueName, message);

		}
	}

	public void receive() throws IOException, TimeoutException, InterruptedException {
		log.info("receive");
		ConnectionFactory connectionFactory = getConnectionFactory();
		try (Connection connection = connectionFactory.newConnection();
			 Channel channel = connection.createChannel()) {

			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
				log.info("received: {}", message);
			};
			channel.basicConsume(QueueName, true, deliverCallback, consumerTag -> {
			});

			log.info("wait");
			aspetta(5);
		}
	}

	public void receiveAck() throws IOException, TimeoutException, InterruptedException {
		log.info("receiveAck");
		ConnectionFactory connectionFactory = getConnectionFactory();
		try (Connection connection = connectionFactory.newConnection();
			 Channel channel = connection.createChannel()) {

			channel.basicQos(1);
			DeliverCallback deliverCallback = (consumerTag, delivery) -> {
				String message = new String(delivery.getBody(), "UTF-8");
				log.info("received: {}", message);
				if (Router.ERROR.name().equals(message)) {
					log.info("basicNack");
					channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
				} else {
					log.info("basicAck");
					channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				}
			};
			channel.basicConsume(QueueName, false, deliverCallback, consumerTag -> {
			});

			log.info("wait");
			aspetta(5);
		}
	}

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		log.info("START");
		TutorOne main = new TutorOne();
		// main.sendDTO(new StockDTO(Router.CHECK.name(), "Check Mess").toString());
		// main.sendDTO(new StockDTO(Router.ERROR.name(), "Error Mess").toString());
		// main.sendDTO("RIGHT");
		main.sendDTO("ERROR");
		main.receiveAck();
		log.info("END");
	}

}
