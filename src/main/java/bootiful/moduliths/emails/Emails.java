package bootiful.moduliths.emails;

import bootiful.moduliths.customers.CustomerCreatedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
class Emails {

    @ApplicationModuleListener
    void customerCreatedEvent(CustomerCreatedEvent cce) {
        System.out.println("going to send an email to welcome the new customer [" + cce + "]");
    }
}
