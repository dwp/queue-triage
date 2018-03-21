package uk.gov.dwp.vault.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import uk.gov.dwp.vault.SensitiveConfigValueLookupRegistry;
import uk.gov.dwp.vault.core.env.VaultPropertySource;

import java.util.Map;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class VaultPropertySourcesAutoConfiguration {

//    @Bean
//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    public PropertySource propertySource(ConfigurableEnvironment env, SensitiveConfigValueLookupRegistry registry){
//        VaultPropertySource vaultPropertySource = new VaultPropertySource(registry);
//        env.getPropertySources().addLast(vaultPropertySource);
//        return vaultPropertySource;
//    }

    @Bean
    public VaultPropertiesBeanPostProcessor vaultPropertiesBeanPostProcessor() {
        return new VaultPropertiesBeanPostProcessor();
    }


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(ConfigurableEnvironment configurableEnvironment, SensitiveConfigValueLookupRegistry registry) {
        PropertySourcesPlaceholderConfigurer vaultPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();

        MutablePropertySources mutablePropertySources = configurableEnvironment.getPropertySources();
        Map<String, Object> collect = StreamSupport.stream(mutablePropertySources.spliterator(), false)
            .filter(c -> c instanceof MapPropertySource)
            .map(i -> ((MapPropertySource) i).getSource())
            .flatMap(g -> g.entrySet().stream())
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        mutablePropertySources.addFirst(new VaultPropertySource(collect.entrySet()
                                                                    .stream()
                                                                    .filter(item -> item.getValue().toString().startsWith("VAULT("))
                                                                    .collect(toMap(Map.Entry::getKey,
                                                                                   Map.Entry::getValue))));

        vaultPlaceholderConfigurer.setPropertySources(mutablePropertySources);
//        vaultPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
        return vaultPlaceholderConfigurer;
    }


}
