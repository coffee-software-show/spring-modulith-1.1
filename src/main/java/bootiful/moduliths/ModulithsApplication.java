package bootiful.moduliths;

import bootiful.moduliths.customers.CustomerCreatedEvent;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * do not forget this!
 */
@RegisterReflectionForBinding(CustomerCreatedEvent.class)
@SpringBootApplication
public class ModulithsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModulithsApplication.class, args);
    }
}
