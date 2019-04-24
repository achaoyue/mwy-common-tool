package com.mwy.chain;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

/**
 * @author mwy
 */
@Aspect
@Slf4j
public class MethodChainAspect {
    InheritableThreadLocal inheritableThreadLocal = new InheritableThreadLocal();
    ThreadLocal threadLocal = new ThreadLocal();

    public MethodChainAspect() {

    }

    @Pointcut("execution(public * com.souche..*.*(..))")
    @Order(1)
    public void pointCut(){};

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        ThreadNode pNode = (ThreadNode) threadLocal.get();

        ThreadNode threadNode = new ThreadNode();

        String fullPath = joinPoint.getSignature().toLongString();

        if(pNode != null){
            threadNode.setMainId(pNode.getMainId());
            threadNode.setParentId(pNode.getSelfId());
        }else {
            threadNode.setMainId(0);
            threadNode.setParentId(0);
        }
        threadNode.setSelfId(1);
        threadNode.setClassName(fullPath);

        threadLocal.set(threadNode);
        logThreadNode(threadNode);
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throw  throwable;
        } finally {
            threadLocal.remove();
            threadLocal.set(pNode);
        }
    }

    private void logThreadNode(ThreadNode threadNode) {
        log.info("method chain :{}",threadNode);
    }
}
