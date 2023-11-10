package it.dgs.queuemanager.queue;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import it.dgs.queuemanager.config.RabbitAnnotation;
import it.dgs.queuemanager.dto.Router;
import it.dgs.queuemanager.dto.StockDTO;
import it.dgs.queuemanager.exception.BusinessException;
import it.dgs.queuemanager.store.GestoreScarti;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile("queue.listener")
public class QueueChecker extends AbstractConsumer {

	private static final String TYPE = Router.CHECK.name();
	private static final Integer DELAY = 1;
	private static final String HeaderProp = "timestamp";

	@Autowired
	private GestoreScarti gestoreScarti;

	private boolean checkRetry(Message message) {
		boolean ret = true;
		MessageHeaders head = message.getHeaders();
		Long deliveryTag = (Long) head.get("amqp_deliveryTag");
		log.info("deliveryTag: {}", deliveryTag);
		ret = deliveryTag < 10L;
		return ret;
	}

	// @RabbitListener(queues = "${queue.checker}")
	public void receive(StockDTO dto) throws Exception {
		log.info("receive: {}", dto);
		log.info("aspetta: {}", DELAY);
		aspetta(DELAY);
		log.info("done");
		if (TYPE.equals(dto.getRoute())) {
			log.info("(" + TYPE + ") receive message: {}", dto);
		} else {
			log.warn("(" + TYPE + ") reject message: {}", dto);
			gestoreScarti.registraScarti(TYPE, dto);
			throw new BusinessException("It is not mine (" + TYPE + "): receive " + dto.toString());
		}
	}

	@RabbitAnnotation
	@RabbitListener(id = "${queue.checker}", queues = "${queue.checker}", containerFactory = "rabbitListenerManualAckContainerFactory", autoStartup = "false")
	public void receive(StockDTO dto//
			, Channel channel//
			, @Header(AmqpHeaders.DELIVERY_TAG) long tag//
			, @Payload Message message) throws Exception {
		log.info("receive: {}", dto);
		log.info("message: {} ", message);
		log.info("headers: {} ", message.getHeaders());
		log.info("payload: {} ", message.getPayload());
		log.info("aspetta: {}", DELAY);
		aspetta(DELAY);
		log.info("done");
		if (TYPE.equals(dto.getRoute())) {
			log.info("(" + TYPE + ") receive message: {}", dto);
			channel.basicAck(tag, false);
		} else {
			log.warn("(" + TYPE + ") reject message: {}", dto);
			gestoreScarti.registraScarti(TYPE, dto);
			channel.basicNack(tag, false, checkRetry(message));
		}
	}

	// @RabbitListener(queues = "${queue.checker}")
	public void receive(@Payload Message message) {
		log.info("message: {} ", message);
		log.info("headers: {} ", message.getHeaders());
		log.info("payload: {} ", message.getPayload());
		log.info("find: {}={}", HeaderProp, String.valueOf(message.getHeaders().get(HeaderProp)));
	}

}