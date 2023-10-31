package bootiful.moduliths.inventory;

import bootiful.moduliths.orders.OrderPlacedEvent;
import bootiful.moduliths.products.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * the intersection between orders and products
 */
@Service
@Transactional
class Stock {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final JdbcClient jdbc;

    Stock(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @ApplicationModuleListener
    void updateInventoryOn(OrderPlacedEvent ope) {

        log.info("order placed! " + ope.toString());

        this.jdbc.sql(
                        """
                                insert into stock (
                                  product_fk  
                                )
                                values (  ?  )
                                on conflict on constraint stock_product_fk_key do update
                                 set quantity_in_stock = stock.quantity_in_stock - 1
                                """
                )
                .param(ope.productId())
                .update();

        log.info("finished write to stock table");

    }

    int stockInInventoryFor(Integer productId) {
        var inv = this.jdbc
                .sql("select quantity_in_stock from stock where product_fk = ? ")
                .param(productId)
                .query((rs, i) -> rs.getInt("quantity_in_stock"))
                .list();
        if (!inv.isEmpty()) {
            var ct = inv.iterator().next();
            return ct;
        }
        return 0;

    }
}

@Controller
class StockController {

    final Stock stock;

    StockController(Stock stock) {
        this.stock = stock;
    }

    // todo why is this value in particular wrong?/

    @SchemaMapping(typeName = "Product")
    int inStock(Product product) {
        return this.stock.stockInInventoryFor(product.id());
    }
}