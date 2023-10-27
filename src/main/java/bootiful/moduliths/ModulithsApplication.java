package bootiful.moduliths;

import bootiful.moduliths.customers.CustomerCreatedEvent;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;


/**
 * do not forget this!
 */
@RegisterReflectionForBinding(CustomerCreatedEvent.class)
@SpringBootApplication
public class ModulithsApplication {

    @Bean
    static Hints hints() {
        return new Hints();
    }

    static class Hints implements BeanFactoryInitializationAotProcessor {

        @Override
        public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
            var toRegister = new HashSet<Class<?>>();
            for (var beanName : beanFactory.getBeanDefinitionNames()) {
                var bd = beanFactory.getBeanDefinition(beanName);
                var clzz = bd.getBeanClassName();
                if (StringUtils.hasText(clzz)) {
                    try {
                        var clzzLiteral = Class.forName(clzz);
                        ReflectionUtils.doWithMethods(clzzLiteral, method -> {
                            var annotations = method.getAnnotation(ApplicationModuleListener.class);
                            if (annotations != null) {
                                toRegister.addAll(Arrays.asList(method.getParameterTypes()));
                            }
                        });
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return (generationContext, beanFactoryInitializationCode) -> {
                for (var c : toRegister) {
                    System.out.println("registering " + c.getName() + " for reflection");
                    generationContext.getRuntimeHints().reflection().registerType(c, MemberCategory.values());
                }
            };
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ModulithsApplication.class, args);
    }
}
