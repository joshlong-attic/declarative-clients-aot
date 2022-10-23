package bootiful.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringValueResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpClientAdapter;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Collection;

@SpringBootApplication
public class HttpApplication   implements EmbeddedValueResolverAware {

    public static void main(String[] args) {
        SpringApplication.run(HttpApplication.class, args);
    }

    @Bean
    WorldBankCurrencyClient worldBankCurrencyApi(HttpServiceProxyFactory httpServiceProxyFactory) {
        return httpServiceProxyFactory
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

    @Bean
    HttpServiceProxyFactory httpServiceProxyFactory(  ReactiveAdapterRegistry registry , ConversionService conversionService , HttpClientAdapter adapter) {
        return HttpServiceProxyFactory
                .builder(adapter)
                .conversionService(conversionService)
                .reactiveAdapterRegistry(registry)
                .embeddedValueResolver(resolver)
                .build();
    }


    private StringValueResolver resolver ;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver  =resolver ;
    }
}


interface WorldBankCurrencyClient {

    @GetMapping("/countries?format=json")
    String currencies();
}


record Currencies(Collection<Currency> currencies) {
}

record Currency(String id, String iso2Code, String name, Region region,
                @JsonProperty("adminregino") Region adminRegion) {
}

record Region(String id, String iso2Code, String value) {
}