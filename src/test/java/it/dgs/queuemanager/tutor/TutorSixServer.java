package it.dgs.queuemanager.tutor;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class TutorSixServer implements AutoCloseable {

	private static Logger log = LoggerFactory.getLogger(TutorSixServer.class);

	private Connection connection;
	private Channel channel;

	public TutorSixServer() throws IOException, TimeoutException {
		ConnectionFactory connectionFactory = TutorSix.getConnectionFactory();
		connection = connectionFactory.newConnection();
		channel = connection.createChannel();

		channel.queueDeclare(TutorSix.RequestQueueName, false, false, false, null);
		channel.queuePurge(TutorSix.RequestQueueName);
		channel.basicQos(1);
	}

	@Override
	public void close() throws Exception {
		connection.close();
	}

	private static int fib(int n) {
		if (n == 0) return 0;
		if (n == 1) return 1;
		return fib(n - 1) + fib(n - 2);
	}

	public void start() throws IOException, TimeoutException, InterruptedException, ExecutionException {
		log.info("start");

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			AMQP.BasicProperties replyProps = new AMQP.BasicProperties
					.Builder()
					.correlationId(delivery.getProperties().getCorrelationId())
					.build();

			String response = "";
			try {
				String message = new String(delivery.getBody(), "UTF-8");
				int n = Integer.parseInt(message);

				log.info(" [.] fib(" + message + ")");
				response += fib(n);
			} catch (RuntimeException e) {
				log.error("Error", e);
			} finally {
				channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			}
		};

		channel.basicConsume(TutorSix.RequestQueueName, false, deliverCallback, (consumerTag -> {
		}));
	}

}
