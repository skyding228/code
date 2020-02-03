package fun.hereis.code.spring.lock;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * 分布式同步
 *
 * @author weichunhe
 * created at 2018/11/30
 */
@Aspect
public class SynchronizedAOP {

    private static Logger LOG = LoggerFactory.getLogger(SynchronizedAOP.class);

    private String LOCK_PREFIX = "synchronized:";


    @Pointcut("@annotation(fun.hereis.code.spring.lock.Synchronized)")
    public void pointcut() {
    }

    private String getKey(JoinPoint joinPoint, Synchronized sync) {
        String key = sync.value();
        if (StringUtils.isEmpty(key)) {
            key = getFullMethodName(joinPoint);
        }
        return LOCK_PREFIX + key;
    }

    @Around("pointcut()&&@annotation(sync)")
    public void around(ProceedingJoinPoint joinPoint, Synchronized sync) {
        String key = getKey(joinPoint, sync);
        String value = UUID.randomUUID().toString();
        String methodName = getFullMethodName(joinPoint);
        boolean locked = DistributedLock.lock(key, value, sync.timeoutSeconds());
        long executionTime = sync.minExecuteSeconds() * 1000;
        long start = System.currentTimeMillis();
        if (locked) {
            LOG.info("lock {} to {}", key, value);
            try {
                joinPoint.proceed(joinPoint.getArgs());
                long remain = System.currentTimeMillis() - start - executionTime;
                if(remain > 0){
                    Thread.sleep(remain);
                }
            } catch (Throwable throwable) {
                LOG.error("An error occurs while executing {} ", methodName, throwable);
            } finally {
                unlock(key, value);
            }
        } else {
            LOG.warn("It will do nothing because lock {} failed.", methodName);
        }
    }

    public void unlock(String key, String value) {
        if (DistributedLock.unlock(key, value)) {
            LOG.info("unlock {} from {}", key, value);
        } else {
            LOG.warn("unlock {} form {} failed.Maybe something is wrong.");
        }
    }

    static String getFullMethodName(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return joinPoint.getTarget().getClass().getName() + "." + signature.getName();
    }
}
