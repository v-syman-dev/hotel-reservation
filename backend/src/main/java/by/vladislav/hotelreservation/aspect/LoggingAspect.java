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

  @Around("execution(* by.vladislav.hotelreservation.service..*(..))")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().toShortString();
    long start = System.nanoTime();

    try {
      Object result = joinPoint.proceed();
      long executionTime = (System.nanoTime() - start) / 1_000_000;
      log.info("Method {} completed in {} ms", methodName, executionTime);
      return result;
    } catch (Throwable ex) {
      long executionTime = (System.nanoTime() - start) / 1_000_000;
      log.error("Method {} failed after {} ms: {}", methodName, executionTime, ex.getMessage());
      throw ex;
    }
  }
}
