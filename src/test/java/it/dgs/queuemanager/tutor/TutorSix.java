package it.dgs.queuemanager.tutor;

import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TutorSix {

	private static Logger log = LoggerFactory.getLogger(TutorSix.class);

	private final static String SpringRabbitmqHost = "localhost";
	private final static String SpringRabbitmqUsername = "guest";
	private final static String SpringRabbitmqPassword = "guest";
	private final static String SpringRabbitmqVirtualHost = "code";

	public static final String RequestQueueName = "tutor.six.quc";

	public static ConnectionFactory getConnectionFactory() {
		ConnectionFactory connectionFactory = new ConnectionFactory();
		connectionFactory.setHost(SpringRabbitmqHost);
		connectionFactory.setUsername(SpringRabbitmqUsername);
		connectionFactory.setPassword(SpringRabbitmqPassword);
		connectionFactory.setVirtualHost(SpringRabbitmqVirtualHost);
		return connectionFactory;
	}

	public static void aspetta(int seconds) throws InterruptedException {
		TimeUnit.SECONDS.sleep(seconds);
	}

	public static void main(String[] args) throws Exception {
		log.info("START");

		try (TutorSixServer server = new TutorSixServer();
			 TutorSixClient client = new TutorSixClient()) {
			server.start();
			for (int i = 0; i < 10; i++) {
				TutorSix.aspetta(2);
				String i_str = Integer.toString(i);
				log.info(" [x] Requesting fib(" + i_str + ")");
				String response = client.call(i_str);
				log.info(" [.] Got '" + response + "'");
			}
		} catch (IOException | TimeoutException | InterruptedException | ExecutionException e) {
			log.error("Error", e);
		}

		log.info("END");
	}

}
