package bootiful.moduliths.customers;

import org.springframework.modulith.events.Externalized;

@Externalized
public record CustomerCreatedEvent(Integer id, String firstName, String lastName, String username) {
}
