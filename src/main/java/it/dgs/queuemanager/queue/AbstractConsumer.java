package it.dgs.queuemanager.queue;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.rabbitmq.client.Channel;

import it.dgs.queuemanager.dto.StockDTO;
import it.dgs.queuemanager.exception.BusinessException;
import it.dgs.queuemanager.store.GestoreScarti;

public abstract class AbstractConsumer {

	@Autowired
	private RabbitTemplate template;

	@Autowired
	private GestoreScarti gestoreScarti;

	void aspetta(int seconds) throws InterruptedException {
		TimeUnit.SECONDS.sleep(seconds);
	}

	void send(String queue, StockDTO message) {
		template.convertAndSend(queue, message);
	}

	void receive(StockDTO dto//
			, String queue//
			, String TYPE//
			, Integer DELAY//
			, Logger log//
	) throws Exception {
		log.info("receive: {}", dto);
		log.info("aspetta: {}", DELAY);
		aspetta(DELAY);
		log.info("done");
		if (TYPE.equals(dto.getRoute())) {
			log.info("(" + TYPE + ") send on queue: {}, message: {}", queue, dto);
			send(queue, dto);
		} else {
			log.warn("(" + TYPE + ") reject message: {}", dto);
			gestoreScarti.registraScarti(TYPE, dto);
			throw new BusinessException("It is not mine (" + TYPE + "): receive " + dto.toString());
		}
	}

	void receive(StockDTO dto//
			, Channel channel//
			, long tag//
			, String queue//
			, String TYPE//
			, Integer DELAY//
			, Logger log//
	) throws Exception {
		log.info("receive: {}", dto);
		log.info("aspetta: {}", DELAY);
		aspetta(DELAY);
		log.info("done");
		if (TYPE.equals(dto.getRoute())) {
			log.info("(" + TYPE + ") send on queue: {}, message: {}", queue, dto);
			send(queue, dto);
			channel.basicAck(tag, false);
		} else {
			log.warn("(" + TYPE + ") reject message: {}", dto);
			gestoreScarti.registraScarti(TYPE, dto);
			channel.basicNack(tag, false, true);
		}
	}

}