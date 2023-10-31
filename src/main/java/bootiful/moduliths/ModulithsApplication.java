package bootiful.moduliths;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.graphql.client.HttpGraphQlClient;
import reactor.core.Disposable;

import java.util.Map;
import java.util.Objects;

@SpringBootApplication
public class ModulithsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModulithsApplication.class, args);
    }

    @Bean
    HttpGraphQlClient httpGraphQlClient() {
        return HttpGraphQlClient.builder().url("http://127.0.0.1:8080/graphql").build();
    }


    @Bean
    ApplicationRunner applicationRunner(HttpGraphQlClient http) {
        return args -> {

            var ptr = new ParameterizedTypeReference<Map<String, Object>>() {
            };

            var ps = http.mutate()
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
                    .map(map ->  map.get("id"))
                    .flatMap(customerId -> http
                            .mutate()
                            .build()
                            .document("""
                               mutation{ 
                                placeOrder (customerId: %s, productId: 1) { id } 
                               }
                            """.formatted(customerId))
                            .retrieve("placeOrder")
                            .toEntity(ptr))
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
                                    .toEntity(ptr)

                    );


            Disposable subscribe = ps.take(0)
                    .subscribe(
                            map -> System.out.println(map.toString())
                    );

        };
    }
}
