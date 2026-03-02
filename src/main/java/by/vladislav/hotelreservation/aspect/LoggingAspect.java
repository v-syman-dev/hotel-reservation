package by.vladislav.hotelreservation.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
  @Around("execution(* by.vladislav.hotelreservation.service.*.*(..))")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();

    Object result = joinPoint.proceed();

    long executionTime = System.currentTimeMillis() - start;

    log.info("Method {} completed in {} ms", joinPoint.getSignature().toShortString(), executionTime);

    return result;
  }
}
