package bootiful.moduliths.products;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("products")
public record Product(@Id Integer id, String sku) {
}
