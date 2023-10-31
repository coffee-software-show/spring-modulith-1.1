package bootiful.moduliths.inventory;

import bootiful.moduliths.orders.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

/**
 * the intersection between orders and products
 */
@Service
class Stock {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JdbcClient jdbc;

    Stock(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @ApplicationModuleListener
    void updateInventoryOn(OrderPlacedEvent ope) {
        logger.info("somebody placed an order so" +
            " we should update the stock inventory here [" + ope + "]");
        this.jdbc.sql("""
                insert into stock (
                  product_fk  
                )
                values (  ?  )
                on conflict on constraint stock_product_fk_key do update
                 set quantity_in_stock = excluded.quantity_in_stock - 1
                                
                                
                """)
                .param( ope.productId())
                .update();

    }

}
