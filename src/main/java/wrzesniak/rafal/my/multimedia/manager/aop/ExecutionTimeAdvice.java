package wrzesniak.rafal.my.multimedia.manager.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@ConditionalOnExpression("${aspect.enabled:true}")
public class ExecutionTimeAdvice {

    @Around("@annotation(wrzesniak.rafal.my.multimedia.manager.aop.TrackExecutionTime)")
    public Object executionTime(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object object = point.proceed();
        long endTime = System.currentTimeMillis();
        Signature signature = point.getSignature();
        log.info("Method: {}::{}{}, execution time: {} s", signature.getDeclaringType().getSimpleName(),
                signature.getName(), point.getArgs(), (endTime-startTime) / 1000);
        return object;
    }

}
