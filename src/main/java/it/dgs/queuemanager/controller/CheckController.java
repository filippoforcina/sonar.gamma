package it.dgs.queuemanager.controller;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import it.dgs.queuemanager.dto.StockDTO;
import it.dgs.queuemanager.store.GestoreScarti;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class CheckController {

	@Autowired
	private GestoreScarti gestoreScarti;

	@Autowired
	private ConnectionFactory clientConnectionFactory;

	@Autowired
	private RabbitListenerEndpointRegistry registry;

	@Value("${queue.checker}")
	private String queue_checker;

	@GetMapping("/")
	public ResponseEntity<String> hello() {
		String mess = "Hello World!!!";
		log.info("mess: {}", mess);
		return ResponseEntity.ok(mess);
	}

	@GetMapping("/startListener")
	public ResponseEntity<String> startListener() {
		String mess = "Start Listener";
		log.info("{}: {}", mess, queue_checker);
		try (Connection connection = clientConnectionFactory.newConnection(); Channel channel = connection.createChannel()) {
			channel.queueDeclare(queue_checker, true, false, false, null);
			registry.getListenerContainer(queue_checker).start();
		} catch (Exception e) {
			log.error("##### ERROR ###### {}", e.toString());
			mess = e.toString();
		}
		return ResponseEntity.ok(mess);
	}

	@GetMapping("/stopListener")
	public ResponseEntity<String> stopListener() {
		String mess = "Stop Listener";
		log.info("{}: {}", mess, queue_checker);
		try {
			registry.getListenerContainer(queue_checker).stop();
		} catch (Exception e) {
			log.error("##### ERROR ###### {}", e.toString());
			mess = e.toString();
		}
		return ResponseEntity.ok(mess);
	}

	@GetMapping("/mappaScarti")
	public ResponseEntity<Map<String, List<StockDTO>>> mappaScarti() {
		Map<String, List<StockDTO>> mappa = gestoreScarti.getMappa();
		log.info("function: {}", mappa);
		return ResponseEntity.ok(mappa);
	}

}