package fun.hereis.code.spring.hotrefresh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 手动注入配置信息
 * @author weichunhe
 * created at 2021/1/29
 */
public class ManualInject {

    private Set<Field> fields = new HashSet<>();

    private Set<Method> methods = new HashSet<>();


    public void add(Field field){
        fields.add(field);
    }

    public void add(Method method){
        methods.add(method);
    }

    /**
     * 注入target中添加autowired的属性和方法
     * 1. 设置类型为bean对应的类型的属性
     * 2. 只有一个参数且类型为bean对应的类型
     * @param target
     * @param bean
     */
    public void wire(Object target, Object bean){
        for (Field field : fields) {
            if(!field.getType().isAssignableFrom(bean.getClass())){
                continue;
            }
            ReflectionUtils.makeAccessible(field);
            try {
                field.set(target,bean);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        for (Method method : methods) {
            if(method.getParameterCount() != 1 || !method.getParameterTypes()[0].isAssignableFrom(bean.getClass())){
                continue;
            }
            ReflectionUtils.makeAccessible(method);
            try {
                method.invoke(target,bean);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }


    private static AnnotationAttributes findAutowiredAnnotation(AccessibleObject ao) {
        if (ao.getAnnotations().length > 0) {  // autowiring annotations have to be local
            AnnotationAttributes attributes = AnnotatedElementUtils.getMergedAnnotationAttributes(ao, Autowired.class);
            if (attributes != null) {
                return attributes;
            }
        }
        return null;
    }

    /**
     * 扫描所有的方法和属性找到添加了Autowired注解的方法及属性，包含父类
     * @param clazz 需要扫描的类
     * @return 手动注入相关信息
     */
    public static ManualInject buildAutowiringMetadata(final Class<?> clazz) {

        Class<?> targetClass = clazz;
        ManualInject manualwiredMetadata = new ManualInject();
        do {

            ReflectionUtils.doWithLocalFields(targetClass, new ReflectionUtils.FieldCallback() {
                @Override
                public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                    AnnotationAttributes ann = findAutowiredAnnotation(field);
                    if (ann != null) {
                        if (Modifier.isStatic(field.getModifiers())) {
                            return;
                        }
                        manualwiredMetadata.add(field);
                    }
                }
            });

            ReflectionUtils.doWithLocalMethods(targetClass, new ReflectionUtils.MethodCallback() {
                @Override
                public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                    Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
                    if (!BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod)) {
                        return;
                    }
                    AnnotationAttributes ann = findAutowiredAnnotation(bridgedMethod);
                    if (ann != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
                        if (Modifier.isStatic(method.getModifiers())) {
                            return;
                        }
                        if (method.getParameterTypes().length == 0) {

                        }
                        manualwiredMetadata.add(method);
                    }
                }
            });


            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);

        return manualwiredMetadata;
    }
}
