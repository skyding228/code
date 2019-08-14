package fun.hereis.code.rocketmq;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 处理spring容器中包含{@link RocketmqListener}注解的方法
 * @author weichunhe
 * created at 2019/8/14
 */
@Component
public class RocketmqBeanPostProcessor  implements BeanPostProcessor {

    @Value("${rocketmq.namesrv.addr}")
    private String nameSrv;

    private final Set<Class<?>> nonAnnotatedClasses =
            Collections.newSetFromMap(new ConcurrentHashMap<Class<?>, Boolean>(64));
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        if (!this.nonAnnotatedClasses.contains(targetClass)) {
            Map<Method, Set<RocketmqListener>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                    new MethodIntrospector.MetadataLookup<Set<RocketmqListener>>() {
                        @Override
                        public Set<RocketmqListener> inspect(Method method) {
                            Set<RocketmqListener> methods = AnnotatedElementUtils.getAllMergedAnnotations(
                                    method, RocketmqListener.class);
                            return (!methods.isEmpty() ? methods : null);
                        }
                    });
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
            }else {
                // Non-empty set of methods
                for (Map.Entry<Method, Set<RocketmqListener>> entry : annotatedMethods.entrySet()) {
                    Method method = entry.getKey();
                    for (RocketmqListener annotation : entry.getValue()) {
                        ConsumerFactory.initConsumer(annotation, method, bean,nameSrv);
                    }
                }
            }
        }
        return bean;
    }
}
