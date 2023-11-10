package it.dgs.queuemanager.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Aspect
public class RabbitAspect {

	@Around("@annotation(RabbitAnnotation)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long start = System.currentTimeMillis();
		Object proceed = joinPoint.proceed();
		long executionTime = System.currentTimeMillis() - start;
		log.info("#### Executing: {} executed in {}ms", joinPoint.getSignature().getName(), executionTime);
		return proceed;
	}

	@Before("getQueueListener()")
	public void loggingAdvice(JoinPoint jp) {
		log.info("#### Executing: {}", jp.getSignature());
	}

	@Pointcut("execution(* it.dgs.queuemanager.queue.*.*(..))")
	public void getQueueListener() {
	}

}
