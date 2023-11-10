package it.dgs.queuemanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import it.dgs.queuemanager.dto.Router;
import it.dgs.queuemanager.dto.StockDTO;
import it.dgs.queuemanager.utils.RabbitUtil;

public class QueueCheckerLoader {

	private static Logger log = LoggerFactory.getLogger(QueueCheckerLoader.class);

	private static final String queue = "checker";

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
		QueueCheckerLoader main = new QueueCheckerLoader();
		main.sendDTO(new StockDTO(Router.CHECK.name(), "Check Mess"));
		// main.sendDTO(new StockDTO(Router.ERROR.name(), "Error Mess"));
		log.info("END");
	}

}
