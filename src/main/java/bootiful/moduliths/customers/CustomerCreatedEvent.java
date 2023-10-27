package bootiful.moduliths.customers;

import org.springframework.modulith.events.Externalized;

import static bootiful.moduliths.customers.CustomersAmqpConfiguration.CUSTOMER_CREATED_DESTINATION_NAME_EXPRESSION;

@Externalized(CUSTOMER_CREATED_DESTINATION_NAME_EXPRESSION)
public record CustomerCreatedEvent(Integer id, String firstName, String lastName, String username) {
}
