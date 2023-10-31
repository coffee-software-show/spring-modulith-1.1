package bootiful.moduliths.products;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

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

interface ProductRepository extends ListCrudRepository<Product, Integer> {

}
