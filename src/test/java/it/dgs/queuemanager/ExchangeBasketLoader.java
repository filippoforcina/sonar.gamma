package it.dgs.queuemanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import it.dgs.queuemanager.dto.Router;
import it.dgs.queuemanager.dto.StockDTO;
import it.dgs.queuemanager.utils.RabbitUtil;

public class ExchangeBasketLoader {

	private static Logger log = LoggerFactory.getLogger(ExchangeBasketLoader.class);

	private static final String exchange = "delivery";
	private static final String routing = "strike";

	public void sendDTO(StockDTO dto) {
		log.info("sendDTO");
		RabbitTemplate rabbitTemplate = new RabbitUtil().getRabbitTemplate();
		log.info("send on exchange: {}, routing: {}, message: {}", exchange, routing, dto);
		rabbitTemplate.convertAndSend(exchange, routing, dto);
		log.info("close connection");
		rabbitTemplate.getConnectionFactory().resetConnection();
	}

	public static void main(String[] args) {
		log.info("START");
		ExchangeBasketLoader main = new ExchangeBasketLoader();
		//main.sendDTO(new StockDTO(Router.BASKET.name(), "Basket Mess"));
		main.sendDTO(new StockDTO(Router.ERROR.name(), "Error Mess"));
		log.info("END");
	}

}
