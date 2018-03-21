package uk.gov.dwp.vault.config;

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
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import uk.gov.dwp.vault.core.env.VaultPropertiesConfigurationFactory;

import java.util.Iterator;

public class VaultPropertiesBeanPostProcessor implements BeanPostProcessor,
                                                         BeanFactoryAware, EnvironmentAware, ApplicationContextAware, InitializingBean, PriorityOrdered {

    private static final Logger logger = LoggerFactory.getLogger(VaultPropertiesBeanPostProcessor.class);
    private BeanFactory beanFactory;
    private PropertySources propertySources;
    private ApplicationContext applicationContext;
    private Environment environment;

    private int order = Ordered.HIGHEST_PRECEDENCE +2;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.propertySources == null) {
            this.propertySources = deducePropertySources();
        }

    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        ConfigurationProperties annotation = AnnotationUtils
            .findAnnotation(bean.getClass(), ConfigurationProperties.class);
        if (annotation != null) {
            postProcessBeforeInitialization(bean, beanName, annotation);
        }
//        annotation = this.beans.findFactoryAnnotation(beanName,
//                                                      ConfigurationProperties.class);
//        if (annotation != null) {
//            postProcessBeforeInitialization(bean, beanName, annotation);
//        }
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
    public void setEnvironment(Environment environment) {
        this.environment = environment;

    }

    @Override
    public int getOrder() {
        return order;
    }

    private void postProcessBeforeInitialization(Object bean, String beanName,
                                                 ConfigurationProperties annotation) {
        Object target = bean;
        VaultPropertiesConfigurationFactory<Object> factory = new VaultPropertiesConfigurationFactory<>(
            target);

        MutablePropertySources vaultPropertySources = new MutablePropertySources();
        vaultPropertySources.addFirst(propertySources.get("vaultPropertySource"));

        factory.setPropertySources(vaultPropertySources);

        if (annotation != null) {
            factory.setIgnoreInvalidFields(annotation.ignoreInvalidFields());
            factory.setIgnoreUnknownFields(annotation.ignoreUnknownFields());
            factory.setIgnoreNestedProperties(annotation.ignoreNestedProperties());
            if (StringUtils.hasLength(annotation.prefix())) {
                factory.setTargetName(annotation.prefix());
            }
        }
        try {
            factory.bindPropertiesToTarget();
        } catch (Exception ex) {
            String targetClass = ClassUtils.getShortName(target.getClass());
            throw new BeanCreationException(beanName, "Could not bind properties to "
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

    private PropertySources deducePropertySources() {
//        PropertySourcesPlaceholderConfigurer configurer = getSinglePropertySourcesPlaceholderConfigurer();
//        if (configurer != null) {
//            // Flatten the sources into a single list so they can be iterated
//            return new ConfigurationPropertiesBindingPostProcessor.FlatPropertySources(configurer.getAppliedPropertySources());
//        }
        if (this.environment instanceof ConfigurableEnvironment) {
            MutablePropertySources propertySources = ((ConfigurableEnvironment) this.environment)
                .getPropertySources();
            return new FlatPropertySources(propertySources);
        }
        // empty, so not very useful, but fulfils the contract
        logger.warn("Unable to obtain PropertySources from "
                    + "PropertySourcesPlaceholderConfigurer or Environment");
        return new MutablePropertySources();
    }

    private static class FlatPropertySources implements PropertySources {

        private PropertySources propertySources;

        FlatPropertySources(PropertySources propertySources) {
            this.propertySources = propertySources;
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

        @Override
        public PropertySource<?> get(String name) {
            return getFlattened().get(name);
        }

        private MutablePropertySources getFlattened() {
            MutablePropertySources result = new MutablePropertySources();
            for (PropertySource<?> propertySource : this.propertySources) {
                flattenPropertySources(propertySource, result);
            }
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
