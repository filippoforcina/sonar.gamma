package it.dgs.queuemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class QueueManagerApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(QueueManagerApplication.class);
		//application.setAdditionalProfiles("queue.init", "queue.consumer", "queue.listener");
		application.setAdditionalProfiles("queue.listener");
		application.run(args);
	}

}
