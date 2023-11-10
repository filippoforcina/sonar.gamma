package it.dgs.queuemanager.queue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import it.dgs.queuemanager.config.RabbitAnnotation;
import it.dgs.queuemanager.dto.Router;
import it.dgs.queuemanager.dto.StockDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("queue.consumer")
public class ManagerSmallConsumer extends AbstractConsumer {

	private static final String TYPE = Router.SMALL.name();
	private static final Integer DELAY = 1;

	@Value("${queue.manager.sizes}")
	private String queue;

	// @RabbitListener(queues = "${queue.manager.in}")
	public void receive(StockDTO dto) throws Exception {
		receive(dto, queue, TYPE, DELAY, log);
	}

	@RabbitAnnotation
	@RabbitListener(queues = "${queue.manager.in}", containerFactory = "rabbitListenerManualAckContainerFactory")
	public void receive(StockDTO dto//
			, Channel channel//
			, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
		receive(dto, channel, tag, queue, TYPE, DELAY, log);
	}

}