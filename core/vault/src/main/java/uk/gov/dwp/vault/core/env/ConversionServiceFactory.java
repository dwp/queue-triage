package uk.gov.dwp.vault.core.env;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ConversionServiceFactory {

    private ConversionService conversionService;
    private List<Converter<?, ?>> converters = Collections.emptyList();
    private List<GenericConverter> genericConverters = Collections.emptyList();

    private final BeanFactory beanFactory;
    private final ApplicationContext applicationContext;


    ConversionServiceFactory(BeanFactory beanFactory, ApplicationContext applicationContext) {
        this.beanFactory = beanFactory;
        this.applicationContext = applicationContext;
    }

    ConversionService getConversionService() {
        if (conversionService == null) {
            conversionService = getOptionalBean(
                ConfigurableApplicationContext.CONVERSION_SERVICE_BEAN_NAME,
                ConversionService.class)
                .orElse(getDefaultConversionService());
        }

        return conversionService;
    }


    private <T> Optional<T> getOptionalBean(String name, Class<T> type) {
        try {
            return Optional.ofNullable(beanFactory.getBean(name, type));
        } catch (NoSuchBeanDefinitionException ex) {
            return Optional.empty();
        }
    }


    private ConversionService getDefaultConversionService() {

        DefaultConversionService conversionService = new DefaultConversionService();
        this.applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
        for (Converter<?, ?> converter : this.converters) {
            conversionService.addConverter(converter);
        }
        for (GenericConverter genericConverter : this.genericConverters) {
            conversionService.addConverter(genericConverter);
        }
        return conversionService;
    }


    /**
     * A list of custom converters (in addition to the defaults) to use when converting properties for binding.
     *
     * @param converters the converters to set
     */
    @Autowired(required = false)
    @ConfigurationPropertiesBinding
    public void setConverters(List<Converter<?, ?>> converters) {
        this.converters = converters;
    }

    /**
     * A list of custom converters (in addition to the defaults) to use when converting properties for binding.
     *
     * @param converters the converters to set
     */
    @Autowired(required = false)
    @ConfigurationPropertiesBinding
    public void setGenericConverters(List<GenericConverter> converters) {
        this.genericConverters = converters;
    }

}
