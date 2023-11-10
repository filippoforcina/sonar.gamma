package it.dgs.queuemanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import it.dgs.queuemanager.dto.Router;
import it.dgs.queuemanager.dto.StockDTO;
import it.dgs.queuemanager.utils.RabbitUtil;

public class ExchangeVirtualHostLoader {

	private static Logger log = LoggerFactory.getLogger(ExchangeVirtualHostLoader.class);

	private static final String exchange = "train";
	private static final String routing = "travel";

	public void sendDTO(StockDTO dto) {
		log.info("sendDTO");
		RabbitTemplate rabbitTemplate = new RabbitUtil().getRabbitTemplateVirtualHost();
		log.info("send on exchange: {}, routing: {}, message: {}", exchange, routing, dto);
		rabbitTemplate.convertAndSend(exchange, routing, dto);
		log.info("close connection");
		rabbitTemplate.getConnectionFactory().resetConnection();
	}

	public static void main(String[] args) {
		log.info("START");
		ExchangeVirtualHostLoader main = new ExchangeVirtualHostLoader();
		main.sendDTO(new StockDTO(Router.CHECK.name(), "Check Mess"));
		log.info("END");
	}

}
