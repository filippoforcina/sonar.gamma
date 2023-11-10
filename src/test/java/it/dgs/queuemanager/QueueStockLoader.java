package it.dgs.queuemanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import it.dgs.queuemanager.dto.Router;
import it.dgs.queuemanager.dto.StockDTO;
import it.dgs.queuemanager.utils.RabbitUtil;

public class QueueStockLoader {

	private static Logger log = LoggerFactory.getLogger(QueueStockLoader.class);

	private static final String queue = "stock.in";

	public void sendDTO(StockDTO dto) {
		log.info("sendDTO");
		RabbitTemplate rabbitTemplate = new RabbitUtil().getRabbitTemplate();
		log.info("send on queue: {}, message: {}", queue, dto);
		rabbitTemplate.convertAndSend(queue, dto);
		log.info("close connection");
		rabbitTemplate.getConnectionFactory().resetConnection();
	}

	public static void main(String[] args) {
		log.info("START");
		QueueStockLoader main = new QueueStockLoader();
		for (int idx = 1; idx <= 5; idx++) {
			main.sendDTO(new StockDTO(Router.SMALL.name(), "Small " + idx));
		}
		for (int idx = 1; idx <= 5; idx++) {
			main.sendDTO(new StockDTO(Router.MEDIUM.name(), "Medium " + idx));
		}
		for (int idx = 1; idx <= 5; idx++) {
			main.sendDTO(new StockDTO(Router.LARGE.name(), "Large " + idx));
		}
		log.info("END");
	}

}
