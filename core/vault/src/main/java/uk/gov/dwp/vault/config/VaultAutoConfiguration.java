package uk.gov.dwp.vault.config;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import uk.gov.dwp.vault.SensitiveConfigValueLookupRegistry;
import uk.gov.dwp.vault.SensitiveConfigValueLookupStrategy;
import uk.gov.dwp.vault.SingleValueVaultLookupStrategy;
import uk.gov.dwp.vault.VaultApiFactory;
import uk.gov.dwp.vault.core.env.VaultPropertySource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

@Configuration
@ComponentScan(basePackages = "uk.gov.dwp.vault.config")
// Make vault is actually configured for use.
@ConditionalOnProperty(prefix = "vault", name = "address")
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class VaultAutoConfiguration {

    @Bean
    public VaultProperties vaultProperties(ConfigurableEnvironment environment) {
        VaultProperties vaultProperties = new VaultProperties();
        RelaxedDataBinder binder = createDataBinderFor(vaultProperties);

        Map<String, Object> vaultConfigProperties = getConfigValuesFromEnvironmentAsStream(environment)
            .filter(item -> item.getKey().startsWith("vault."))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        binder.bind(new MutablePropertyValues(vaultConfigProperties));

        return vaultProperties;
    }

    @Bean
    public SensitiveConfigValueLookupRegistry sensitiveConfigValueDelegatingService(List<SensitiveConfigValueLookupStrategy> strategies) {
        return new SensitiveConfigValueLookupRegistry(strategies);
    }

    @Bean
    public VaultApiFactory vaultApiFactory() {
        return new VaultApiFactory();
    }

    @Bean
    public SingleValueVaultLookupStrategy singleValueVaultLookupStrategy(VaultApiFactory vaultFactoryApi,
                                                                         VaultProperties vaultProperties) {
        return new SingleValueVaultLookupStrategy(vaultProperties, vaultFactoryApi);
    }


    @Bean
    public VaultPropertySource vaultPropertySource(ConfigurableEnvironment configurableEnvironment, SensitiveConfigValueLookupRegistry registry) {

        Map<String, Object> collect = getConfigValuesFromEnvironmentAsStream(configurableEnvironment)
            .filter(item -> item.getValue().toString().startsWith("VAULT("))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        VaultPropertySource vaultPropertySource = new VaultPropertySource(collect, registry);
        // get property source names which pertain to the application config, so that we can inject the vault stuff in there.
        StreamSupport.stream(configurableEnvironment.getPropertySources().spliterator(), false)
            .map(PropertySource::getName)
            .filter(name -> name.startsWith("applicationConfig"))
            .findFirst()
            .ifPresent(appConfigName -> configurableEnvironment.getPropertySources().addBefore(appConfigName, vaultPropertySource));

        return vaultPropertySource;
    }

    @Bean
    @DependsOn("vaultPropertySource")
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }


    private Converter<char[], String> chararacterArrayToStringConverter() {
        return new Converter<char[], String>() {
            @Override
            public String convert(char[] source) {
                return new String(source);
            }
        };
    }

    @Bean
    public ConversionService conversionService() {
        DefaultConversionService defaultConversionService = new DefaultConversionService();
        defaultConversionService.addConverter(chararacterArrayToStringConverter());
        return defaultConversionService;
    }


    private RelaxedDataBinder createDataBinderFor(VaultProperties vaultProperties) {
        RelaxedDataBinder binder = new RelaxedDataBinder(vaultProperties, "vault");
        binder.setConversionService(new DefaultConversionService());

        binder.setIgnoreUnknownFields(true);
        binder.setIgnoreInvalidFields(false);
        binder.setIgnoreNestedProperties(false);
        return binder;
    }

    private Stream<Map.Entry<String, Object>> getConfigValuesFromEnvironmentAsStream(ConfigurableEnvironment configurableEnvironment) {
        return StreamSupport.stream(configurableEnvironment.getPropertySources().spliterator(), false)
            .filter(c -> c instanceof MapPropertySource)
            .map(i -> ((MapPropertySource) i).getSource())
            .flatMap(g -> g.entrySet().stream());
    }

}
