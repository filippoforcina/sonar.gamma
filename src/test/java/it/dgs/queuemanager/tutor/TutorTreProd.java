package it.dgs.queuemanager.tutor;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import it.dgs.queuemanager.dto.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TutorTreProd {

	private static Logger log = LoggerFactory.getLogger(TutorTreProd.class);

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

	public void sendDTO(String message) throws IOException, TimeoutException {
		log.info("sendDTO");
		ConnectionFactory connectionFactory = getConnectionFactory();
		try (Connection connection = connectionFactory.newConnection();
			 Channel channel = connection.createChannel()) {

			channel.exchangeDeclare(ExchangeName, "fanout");
			log.info("create exchange: {}", ExchangeName);
			channel.basicPublish(ExchangeName, "", null, message.getBytes());
			log.info("send on exchange: {}, message: {}", ExchangeName, message);

		}
	}

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		log.info("START");
		TutorTreProd main = new TutorTreProd();
		main.sendDTO(Router.CHECK.name());
		log.info("END");
	}

}
