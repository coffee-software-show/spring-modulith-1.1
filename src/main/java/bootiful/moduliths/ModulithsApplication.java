package bootiful.moduliths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Collection;
import java.util.Map;

@SpringBootApplication
@EnableAsync
public class ModulithsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModulithsApplication.class, args);
    }

    @Async
    @EventListener (ApplicationReadyEvent.class)
    void async( ) throws Exception {

        Thread.sleep(100);

        var http= HttpGraphQlClient.builder().url("http://127.0.0.1:8080/graphql").build();
        var ptr = new ParameterizedTypeReference<Map<String, Object>>() {
        };

        var productsAfterPlacingOrders = http
                .mutate()
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
                .map(map -> map.get("id"))
                .flatMap(customerId -> http
                        .mutate()
                        .build()
                        .document(
                                """
                                   mutation{ 
                                    placeOrder (customerId: %s, productId: 1) { id } 
                                    placeOrder (customerId: %s, productId: 1) { id } 
                                    placeOrder (customerId: %s, productId: 1) { id } 
                                   }
                                """.formatted(customerId, customerId, customerId))
                        .execute()
                )
                .thenMany(
                        http
                                .mutate()
                                .build()
                                .document(
                                    """
                                        query {
                                            products  {
                                                id , sku , inStock
                                            }
                                        }
                                    """
                                )
                                .retrieve("products")
                                .toEntity(new ParameterizedTypeReference<Collection<Map<String, Object>>>() { })

                );


        productsAfterPlacingOrders.subscribe(listOfMaps -> System.out.println(listOfMaps.toString()));

    }


}
