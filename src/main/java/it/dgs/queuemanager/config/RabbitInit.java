package it.dgs.queuemanager.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("queue.init")
public class RabbitInit {

	@Value("${queue.checker}")
	private String queue_checker;

	@Value("${queue.manager.in}")
	private String queue_manager_in;

	@Value("${queue.manager.split}")
	private String queue_manager_split;

	@Value("${queue.manager.sizes}")
	private String queue_manager_sizes;

	@Value("${queue.manager.sizem}")
	private String queue_manager_sizem;

	@Value("${queue.manager.sizel}")
	private String queue_manager_sizel;

	@Value("${queue.basket}")
	private String queue_basket;

	@Bean
	Queue _queueChecker() {
		return new Queue(queue_checker);
	}

	@Bean
	Queue _queueManagerIn() {
		return new Queue(queue_manager_in);
	}

	@Bean
	Queue _queueManagerSplit() {
		return new Queue(queue_manager_split);
	}

	@Bean
	Queue _queueManagerSizes() {
		return new Queue(queue_manager_sizes);
	}

	@Bean
	Queue _queueManagerSizem() {
		return new Queue(queue_manager_sizem);
	}

	@Bean
	Queue _queueManagerSizel() {
		return new Queue(queue_manager_sizel);
	}

	@Bean
	Queue _queueBasket() {
		return new Queue(queue_basket);
	}

}
