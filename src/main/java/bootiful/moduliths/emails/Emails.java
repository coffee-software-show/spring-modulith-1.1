package bootiful.moduliths.emails;

import bootiful.moduliths.customers.CustomerCreatedEvent;
import bootiful.moduliths.orders.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
class Emails {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@ApplicationModuleListener
	void orderPlaced(OrderPlacedEvent placedEvent) {
		log.info("going to send an email to the customer " + "on their newly placed order [" + placedEvent + "]!");
	}

	@ApplicationModuleListener
	void customerCreatedEvent(CustomerCreatedEvent cce) {
		log.info("going to send an email to welcome the new customer [" + cce + "]");
	}

}
