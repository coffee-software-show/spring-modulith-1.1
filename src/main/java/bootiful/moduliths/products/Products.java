package bootiful.moduliths.products;

import bootiful.moduliths.orders.OrderPlacedEvent;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collection;


@Controller
class ProductController {

    private final ProductRepository repository;

    ProductController(ProductRepository repository) {
        this.repository = repository;
    }

    @QueryMapping
    Collection<Product> products() {
        return this.repository.findAll();
    }
}

@Service
@Transactional
class Products {

    private final ProductRepository repository;

    Products(ProductRepository repository) {
        this.repository = repository;
    }

    @ApplicationModuleListener
    void orderPlaced(OrderPlacedEvent event) {
        this.repository
                .findById(event.productId())
                .ifPresent(product -> {
                    Assert.state(product.inStock() > 0,
                            "there must be at least one of these products available in the inventory");
                    this.repository.save(new Product(product.id(), product.sku(), product.inStock() - 1));
                });
    }

}

interface ProductRepository extends ListCrudRepository<Product, Integer> {
}

@Table("products")
record Product(@Id Integer id, String sku, long inStock) {
}
