package bootiful.moduliths.customers;

public record CustomerCreatedEvent(Integer id, String firstName, String lastName, String username) {
}
