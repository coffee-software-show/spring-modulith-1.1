package bootiful.moduliths.emails;

import bootiful.moduliths.customers.CustomerCreatedEvent;
import bootiful.moduliths.orders.OrderPlacedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
class Emails {

    @ApplicationModuleListener
    void orderPlaced(OrderPlacedEvent placedEvent) {
        System.out.println("going to send an email to the customer " +
                           "on their newly placed order [" + placedEvent + "]!");
    }

    @ApplicationModuleListener
    void customerCreatedEvent(CustomerCreatedEvent cce) {
        System.out.println("going to send an email to welcome the new customer [" + cce + "]");
    }
}
