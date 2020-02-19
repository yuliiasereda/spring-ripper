package quoters;

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class ProfilingHandlerBeanPostProcessor implements BeanPostProcessor {

  private Map<String, Class> map = new HashMap<>(); //the bean name is always saved, unlike the class that can change at the proxy stage
  private ProfilingController controller = new ProfilingController();

  public ProfilingHandlerBeanPostProcessor()
      throws Exception {
    MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
    platformMBeanServer
        .registerMBean(controller, new ObjectName("profiling", "name", "controller"));
  }

  public Object postProcessBeforeInitialization(Object bean,
      String beanName)
      throws BeansException { //memorize classes in map
    Class<?> beanClass = bean.getClass();
    if (beanClass.isAnnotationPresent(Profiling.class)) {
      map.put(beanName, beanClass);
    }
    return bean;
  }

  public Object postProcessAfterInitialization(final Object bean, String beanName)
      throws BeansException { //change classes
    System.out.println("postProcessAfterInitialization method is called");
    Class beanClass = map.get(beanName);
    if (beanClass != null) {
      return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(),
          new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
              if (controller.isEnabled()) {
                System.out.println("PROFILING...");
                long before = System.nanoTime();
                Object retVal = method.invoke(bean, args);
                long after = System.nanoTime();
                System.out.println(after - before);
                System.out.println("END OF PROFILING");
                return retVal;
              } else {
                return method.invoke(bean, args);
              }
            }
          });
    }
    return null;
  }
}
