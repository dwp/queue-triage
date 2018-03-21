package uk.gov.dwp.vault.core.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.Optional;

public class VaultConfigurationPropertiesBindingPostProcessor implements BeanPostProcessor,
                                                                         BeanFactoryAware, ApplicationContextAware, InitializingBean, PriorityOrdered {

    private static final Logger logger = LoggerFactory.getLogger(VaultConfigurationPropertiesBindingPostProcessor.class);
    private BeanFactory beanFactory;
    private FlatPropertySources flattenedPropertySources;
    private ApplicationContext applicationContext;

    private ConversionServiceFactory conversionServiceFactory;

    private int order;

    public VaultConfigurationPropertiesBindingPostProcessor(int order) {
        this.order = order;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (flattenedPropertySources == null) {
            flattenedPropertySources = deducePropertySources();
        }

        if (conversionServiceFactory == null) {
            conversionServiceFactory = new ConversionServiceFactory(beanFactory, applicationContext);
        }

    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        ConfigurationProperties annotation = AnnotationUtils
            .findAnnotation(bean.getClass(), ConfigurationProperties.class);
        if (annotation != null) {
            postProcessBeforeInitialization(bean, beanName, annotation);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    @Override
    public int getOrder() {
        return order;
    }

    private void postProcessBeforeInitialization(Object targetBean, String targetBeanName,
                                                 ConfigurationProperties annotation) {

        String configurationPrefix = annotation.prefix();
        if (StringUtils.hasLength(configurationPrefix)) {
            if (!flattenedPropertySources.containsKeysWithPrefix(configurationPrefix)) {
                logger.warn("Did not find any vault keys that needed resolution. Config prefix was: {} for ConfigurationProperties annotated class: {}", configurationPrefix, targetBean.getClass().getSimpleName());
                return;
            }
        }

        logger.info("About to lookup values for bean: {}", targetBean.getClass().getSimpleName());
        VaultPropertiesConfigurationFactory<Object> factory = new VaultPropertiesConfigurationFactory<>(
            targetBean, flattenedPropertySources, conversionServiceFactory.getConversionService());

        factory.setIgnoreInvalidFields(annotation.ignoreInvalidFields());
        factory.setIgnoreUnknownFields(annotation.ignoreUnknownFields());
        factory.setIgnoreNestedProperties(annotation.ignoreNestedProperties());
        if (StringUtils.hasLength(configurationPrefix)) {
            factory.setTargetName(configurationPrefix);
        }

        try {

            factory.bindPropertiesToTarget();
        } catch (Exception ex) {
            String targetClass = ClassUtils.getShortName(targetBean.getClass());
            throw new BeanCreationException(targetBeanName, "Could not bind properties to "
                                                      + targetClass + " (" + getAnnotationDetails(annotation) + ")", ex);
        }
    }

    private String getAnnotationDetails(ConfigurationProperties annotation) {
        if (annotation == null) {
            return "";
        }
        StringBuilder details = new StringBuilder();
        details.append("prefix=").append(annotation.prefix());
        details.append(", ignoreInvalidFields=").append(annotation.ignoreInvalidFields());
        details.append(", ignoreUnknownFields=").append(annotation.ignoreUnknownFields());
        details.append(", ignoreNestedProperties=")
            .append(annotation.ignoreNestedProperties());
        return details.toString();
    }

    private FlatPropertySources deducePropertySources() {
        VaultPropertySource bean = applicationContext.getBean(VaultPropertySource.class);
        if (bean != null) {
            return new FlatPropertySources(bean);
        }

        // empty, so not very useful, but fulfils the contract
        logger.warn("Unable to obtain PropertySources from "
                    + "PropertySourcesPlaceholderConfigurer or Environment");
        return new FlatPropertySources();
    }

    private static class FlatPropertySources implements PropertySources {

        private VaultPropertySource vaultPropertySource;

        FlatPropertySources(VaultPropertySource propertySources) {
            vaultPropertySource = propertySources;
        }

        FlatPropertySources() {
        }

        @Override
        public Iterator<PropertySource<?>> iterator() {
            MutablePropertySources result = getFlattened();
            return result.iterator();
        }

        @Override
        public boolean contains(String name) {
            return get(name) != null;
        }

        boolean containsKeysWithPrefix(String prefix) {
            return vaultPropertySource.containsPropertiesWithPrefix(prefix);
        }

        @Override
        public PropertySource<?> get(String name) {
            return getFlattened().get(name);
        }

        private MutablePropertySources getFlattened() {
            MutablePropertySources result = new MutablePropertySources();
            Optional.ofNullable(vaultPropertySource).ifPresent(source -> flattenPropertySources(source, result));
            return result;
        }

        private void flattenPropertySources(PropertySource<?> propertySource,
                                            MutablePropertySources result) {
            Object source = propertySource.getSource();
            if (source instanceof ConfigurableEnvironment) {
                ConfigurableEnvironment environment = (ConfigurableEnvironment) source;
                for (PropertySource<?> childSource : environment.getPropertySources()) {
                    flattenPropertySources(childSource, result);
                }
            } else {
                result.addLast(propertySource);
            }
        }

    }
}
