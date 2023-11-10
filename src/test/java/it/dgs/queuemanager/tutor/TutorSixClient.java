package it.dgs.queuemanager.tutor;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class TutorSixClient implements AutoCloseable {

	private static Logger log = LoggerFactory.getLogger(TutorSixClient.class);

	private Connection connection;
	private Channel channel;

	public TutorSixClient() throws IOException, TimeoutException {
		ConnectionFactory connectionFactory = TutorSix.getConnectionFactory();
		connection = connectionFactory.newConnection();
		channel = connection.createChannel();
	}

	@Override
	public void close() throws Exception {
		connection.close();
	}

	public String call(String message) throws IOException, TimeoutException, InterruptedException, ExecutionException {
		log.info("call");

		String queueName = channel.queueDeclare().getQueue();
		log.info("create queue: {}", queueName);

		String corrId = UUID.randomUUID().toString();
		log.info("create corrId: {}", corrId);

		AMQP.BasicProperties props = new AMQP.BasicProperties
				.Builder()
				.correlationId(corrId)
				.replyTo(queueName)
				.build();

		channel.basicPublish("", TutorSix.RequestQueueName, props, message.getBytes("UTF-8"));

		final CompletableFuture<String> response = new CompletableFuture<>();

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			if (delivery.getProperties().getCorrelationId().equals(corrId)) {
				log.info("correct corrId: {}", corrId);
				response.complete(new String(delivery.getBody(), "UTF-8"));
			} else {
				log.warn("wrong corrId: {}", corrId);
			}
		};
		CancelCallback cancelCallback = (consumerTag) -> {
		};
		String ctag = channel.basicConsume(queueName, true, deliverCallback, cancelCallback);

		String result = response.get();
		channel.basicCancel(ctag);

		return result;
	}

}
