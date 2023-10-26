package bootiful.moduliths.customers;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.stream.Stream;

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
class Customers {

    private final TransactionTemplate transactionTemplate;
    private final CustomerRepository repository;
    private final ApplicationEventPublisher publisher;

    Customers(TransactionTemplate transactionTemplate,
              CustomerRepository repository, ApplicationEventPublisher publisher) {
        this.transactionTemplate = transactionTemplate;
        this.repository = repository;
        this.publisher = publisher;
    }

    Customer create(String first, String last, String username) {
        Stream.of(first, last, username).forEach(p -> Assert.hasText(p,
                "you must provide a value, but you provided [" + p + "]"));
        Assert.isTrue(validUsername(username), "your username must contain only valid letters and digits");
        var customer = new Customer(null, first, last, username);
        return this.transactionTemplate
                .execute(status -> {
                    var c = this.repository.save(customer);
                    publisher.publishEvent(new CustomerCreatedEvent(c.id(), c.first(), c.last(), c.username()));
                    return c;
                });
    }

    private boolean validUsername(String username) {
        var nc = new StringBuilder();
        for (var c : username.toCharArray())
            if (Character.isLetterOrDigit(c))
                nc.append(c);
        return username.equalsIgnoreCase(nc.toString());
    }

    Collection<Customer> all() {
        return this.repository.findAll();
    }
}

record Customer(@Id Integer id, String first, String last, String username) {
}


interface CustomerRepository extends ListCrudRepository<Customer, Integer> {
}
