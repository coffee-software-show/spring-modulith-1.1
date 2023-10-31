package bootiful.moduliths;

import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.scheduling.annotation.EnableAsync;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;

@EnableAsync
@SpringBootApplication
public class ModulithsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModulithsApplication.class, args);
	}

}

@Configuration
class DemoClient {

	@Bean
	HttpGraphQlClient httpGraphQlClient() {
		return HttpGraphQlClient.builder().url("http://127.0.0.1:8080/graphql").build();
	}

	@Bean
	ApplicationRunner demo(HttpGraphQlClient http) {
		return args -> {
			var log = LoggerFactory.getLogger(getClass());
			var ptr = new ParameterizedTypeReference<Map<String, Object>>() {
			};
			var productsAfterPlacingOrders = http.mutate()
				.build()
				.document("""
						mutation {
						  createCustomer (first:"Josh",last:"Long" ,username:"jlong"){
						    id
						  }
						}

						""")
				.retrieve("createCustomer")
				.toEntity(ptr)
				.map(map -> Integer.parseInt((String) map.get("id")))
				.flatMapMany(customerId -> all(http, customerId))
				.thenMany(http.mutate()//
					.build()//
					.document("""
							    query {
							        products {
							            id, sku, inStock
							        }
							    }
							""")//
					.retrieve("products")//
					.toEntity(new ParameterizedTypeReference<Collection<Map<String, Object>>>() {
					}));

			productsAfterPlacingOrders.subscribe(listOfMaps -> log.info(listOfMaps.toString()));

		};
	}

	private Mono<Object> placeOrderForCustomerId(HttpGraphQlClient http, Integer customerId) {
		return http.mutate()//
			.build()//
			.document("""
					   mutation{
					    placeOrder (customerId: %s, productId: 1) { id }
					   }
					""".formatted(customerId)) //
			.retrieve("placeOrder")//
			.toEntity(Object.class);
	}

	private Flux<Object> all(HttpGraphQlClient http, Integer customerId) {
		return Flux.range(0, 3).flatMap(i -> placeOrderForCustomerId(http, customerId));
	}

}