package it.dgs.queuemanager.queue;

import com.rabbitmq.client.Channel;

import it.dgs.queuemanager.config.RabbitAnnotation;
import it.dgs.queuemanager.dto.Router;
import it.dgs.queuemanager.dto.StockDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@Profile("queue.consumer")
public class ManagerSplitConsumer extends AbstractConsumer {

	private static final String exchange = "delivery";

	private static final List<String> routes = Arrays.asList(Router.LARGE.name()//
			, Router.MEDIUM.name()//
			, Router.SMALL.name()//
	);

	@Autowired
	private RabbitTemplate template;

	@RabbitAnnotation
	@RabbitListener(queues = "${queue.manager.split}", containerFactory = "rabbitListenerManualAckContainerFactory")
	public void receive(StockDTO dto//
			, Channel channel//
			, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
		log.info("receive: {}", dto);
		if (routes.contains(dto.getRoute())) {
			log.info("send on route: {}, message: {}", dto.getRoute(), dto);
			template.convertAndSend(exchange, dto.getRoute().toLowerCase(), dto);
			channel.basicAck(tag, false);
		} else {
			log.warn("reject message: {}", dto);
			channel.basicNack(tag, false, false);
		}
	}

}