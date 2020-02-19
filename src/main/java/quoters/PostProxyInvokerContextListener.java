package quoters;

import java.lang.reflect.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class PostProxyInvokerContextListener implements ApplicationListener<ContextRefreshedEvent > {

  @Autowired
  private ConfigurableListableBeanFactory factory;
  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    ApplicationContext context = contextRefreshedEvent.getApplicationContext();
    String[] names = context.getBeanDefinitionNames();
    for (String name : names) {
      BeanDefinition beanDefinition = factory.getBeanDefinition(name);
      String originalClassName = beanDefinition.getBeanClassName();
      try {
        Class<?> originalClass = Class.forName(originalClassName);
        Method[] methods = originalClass.getMethods();
        for (Method method : methods) {
          if(method.isAnnotationPresent(PostProxy.class)){
//            method.invoke() through dynamic-proxy will not work, because the method is not called on the current class
            Object bean = context.getBean(name);
            Method currentMethod = bean.getClass()
                .getMethod(method.getName(), method.getParameterTypes());
            currentMethod.invoke(bean);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
  }
}
