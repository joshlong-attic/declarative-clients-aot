package bootiful.http;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@SpringBootApplication
public class HttpApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpApplication.class, args);
    }

    @Bean
    WorldBankCurrencyClient worldBankCurrencyApi(
            HttpClientAdapter webClientAdapter) {
        return HttpServiceProxyFactory
                .builder(webClientAdapter)
                .build()
                .createClient(WorldBankCurrencyClient.class);
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> ready(WorldBankCurrencyClient currencyApi) {
        return event -> System.out.println(currencyApi.currencies());
    }

    @Bean
    HttpClientAdapter httpClientAdapter(WebClient.Builder builder) {
        return WebClientAdapter
                .forClient(builder.baseUrl("http://api.worldbank.org/v2/").build());
    }
}

interface WorldBankCurrencyClient {

    @GetExchange("/countries?format=json")
    String currencies();
}
