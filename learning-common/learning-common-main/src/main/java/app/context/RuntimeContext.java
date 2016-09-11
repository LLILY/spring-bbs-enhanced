package app.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Created by lili19289 on 2016/8/15.
 */
public class RuntimeContext {


    public static ApplicationContext getSpringContext(){
        return SpringContext.getSpringContext();
    }

    public static Object getBean(String name) {
        return getBean(name, true);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return getBean(name, requiredType, true);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return getBean(requiredType, true);
    }

    public static Object getBean(String name, boolean searchInAncestor) {
        Object bean = getSpringContext().getBean(name);
        if (null == bean && searchInAncestor) {
            ApplicationContext parentContext = null;
            while (null == bean && null != (parentContext = getSpringContext().getParent())) {
                bean = parentContext.getBean(name);
            }
        }
        return bean;
    }

    public static <T> T getBean(String name, Class<T> requiredType, boolean searchInAncestor) {
        T bean = getSpringContext().getBean(name, requiredType);
        if (null == bean && searchInAncestor) {
            ApplicationContext parentContext = null;
            while (null == bean && null != (parentContext = getSpringContext().getParent())) {
                bean = parentContext.getBean(name, requiredType);
            }
        }
        return bean;
    }

    public static <T> T getBean(Class<T> requiredType, boolean searchInAncestor) {
        T bean = getSpringContext().getBean(requiredType);
        if (null == bean && searchInAncestor) {
            ApplicationContext parentContext = null;
            while (null == bean && null != (parentContext = getSpringContext().getParent())) {
                bean = parentContext.getBean(requiredType);
            }
        }
        return bean;
    }

    public static class SpringContext implements ApplicationContextAware {

        private static SpringContext springContext = new SpringContext();

        private static ApplicationContext applicationContext;

        private static SpringContext getInstance(){return springContext;}

        /**
         * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
         */
        public void setApplicationContext(ApplicationContext applicationContext) {
            SpringContext.applicationContext = applicationContext; // NOSONAR
        }

        /**
         * 取得存储在静态变量中的ApplicationContext.
         */
        public static ApplicationContext getSpringContext() {
            checkApplicationContext();
            return applicationContext;
        }

        /**
         * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
         */
        @SuppressWarnings("unchecked")
        public static <T> T getBean(String name) {
            checkApplicationContext();
            return (T) applicationContext.getBean(name);
        }

        /**
         * 从静态变量ApplicationContext中取得Bean, 自动转型为所赋值对象的类型.
         */
        @SuppressWarnings("unchecked")
        public static <T> T getBean(Class<T> clazz) {
            checkApplicationContext();
            return (T) applicationContext.getBeansOfType(clazz);
        }

        /**
         * 清除applicationContext静态变量.
         */
        public static void cleanApplicationContext() {
            applicationContext = null;
        }

        private static void checkApplicationContext() {
            if (applicationContext == null) {
                throw new IllegalStateException("applicaitonContext未注入,请在applicationContext.xml中定义SpringContextHandle");
            }
        }
    }
}
