package bootiful.moduliths.customers;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Controller
class CustomerGraphqlController {

    private final Customers customers;

    CustomerGraphqlController(Customers customers) {
        this.customers = customers;
    }

    @QueryMapping
    Collection<Customer> customers() {
        return this.customers.all();
    }

    @MutationMapping
    Customer create(@Argument String first, @Argument String last, @Argument String username) {
        return this.customers.create(first, last, username);
    }

}

@Service
@Transactional
class Customers {

    private final CustomerRepository repository;

    private final ApplicationEventPublisher publisher;

    Customers(CustomerRepository repository, ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    Customer create(String first, String last, String username) {
        var customer = this.repository.save(new Customer(null, first, last, username));
        publisher.publishEvent(new CustomerCreatedEvent(
                customer.id(), customer.first(), customer.last(), customer.username()));
        return customer;
    }

    Collection<Customer> all() {
        return this.repository.findAll();
    }
}

record Customer(@Id Integer id, String first, String last, String username) {
}

interface CustomerRepository extends ListCrudRepository<Customer, Integer> {
}

@Configuration
class CustomersAmqpConfiguration {

    static final String CUSTOMER_CREATED_DESTINATION_NAME = "customer-created-events";

    static final String CUSTOMER_CREATED_DESTINATION_NAME_EXPRESSION =
            CUSTOMER_CREATED_DESTINATION_NAME + "::#{'" + CUSTOMER_CREATED_DESTINATION_NAME + "'}";

    @Bean
    InitializingBean initializingBean(Exchange customersCreatedExchange, Binding customersCreatedBinding, Queue customersCreatedQueue, AmqpAdmin amqpAdmin) {
        return () -> {
            amqpAdmin.declareQueue(customersCreatedQueue);
            amqpAdmin.declareExchange(customersCreatedExchange);
            amqpAdmin.declareBinding(customersCreatedBinding);
        };
    }

    @Bean
    Queue customersCreatedQueue() {
        return QueueBuilder.durable(CUSTOMER_CREATED_DESTINATION_NAME)
                .build();
    }

    @Bean
    Exchange customersCreatedExchange() {
        return ExchangeBuilder.directExchange(CUSTOMER_CREATED_DESTINATION_NAME)
                .build();
    }

    @Bean
    Binding customersCreatedBinding(Queue customersCreatedQueue, Exchange customersCreatedExchange) {
        return BindingBuilder
                .bind(customersCreatedQueue)
                .to(customersCreatedExchange)
                .with(CUSTOMER_CREATED_DESTINATION_NAME)
                .noargs();
    }
}
