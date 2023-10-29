package bootiful.moduliths.orders;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;

@Controller
class OrdersController {

    private final Orders orders;

    OrdersController(Orders orders) {
        this.orders = orders;
    }

    @QueryMapping
    Collection<Order> orders() {
        return this.orders.orders();
    }

    @MutationMapping
    Order placeOrder(
            @Argument Integer customerId,
            @Argument Integer productId) {
        return this.orders.place(customerId, productId);
    }
}

@Service
@Transactional
class Orders {

    private final ApplicationEventPublisher publisher;

    private final OrderRepository repository;

    Orders(ApplicationEventPublisher publisher, OrderRepository repository) {
        this.publisher = publisher;
        this.repository = repository;
    }

    Collection<Order> orders() {
        return this.repository.findAll();
    }

    Order place(Integer customerId, Integer productId) {
        var saved = this.repository.save(new Order(null, customerId, productId));
        this.publisher.publishEvent(new OrderPlacedEvent(saved.id(),
                customerId, productId));
        return saved;
    }
}

interface OrderRepository extends ListCrudRepository<Order, Integer> {
}

@Table("customer_orders")
record Order(@Id Integer id, @Column ("customer_fk") Integer customerId,
             @Column ("product_fk") Integer inventoryId) {
}