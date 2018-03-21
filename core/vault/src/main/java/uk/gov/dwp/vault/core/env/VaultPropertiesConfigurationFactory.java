package uk.gov.dwp.vault.core.env;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.bind.PropertySourcesPropertyValues;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.PropertySources;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;

import java.util.Properties;


/**
 * Binding resolved property values from vault where secret paths are defined
 * in {@link Properties} (or optionally {@link PropertySources})
 * to an object of a specified type
 *
 * @param <T> the target type
 * @author Kevin Potgieter
 */
class VaultPropertiesConfigurationFactory<T>
    implements FactoryBean<T>, InitializingBean {

    private static final Log logger = LogFactory
        .getLog(VaultPropertiesConfigurationFactory.class);

    private boolean ignoreUnknownFields = true;

    private boolean ignoreInvalidFields;

    private boolean exceptionIfInvalid = true;

    private final PropertySources propertySources;

    private final T target;

    private boolean hasBeenBound = false;

    private boolean ignoreNestedProperties = false;

    private String targetName;

    private boolean resolvePlaceholders = true;
    private final ConversionService conversionService;

    /**
     * Create a new {@link VaultPropertiesConfigurationFactory} instance.
     *
     * @param target the target object to bind too
     */
    public VaultPropertiesConfigurationFactory(T target, PropertySources propertySources, ConversionService conversionService) {
        Assert.notNull(target, "target must not be null");
        this.target = target;
        this.propertySources = propertySources;
        this.conversionService = conversionService;
    }

    /**
     * Flag to disable binding of nested properties (i.e. those with period separators in their paths). Can be useful to disable this if the name prefix is empty and you don't want to ignore unknown
     * fields.
     *
     * @param ignoreNestedProperties the flag to set (default false)
     */
    public void setIgnoreNestedProperties(boolean ignoreNestedProperties) {
        this.ignoreNestedProperties = ignoreNestedProperties;
    }

    /**
     * Set whether to ignore unknown fields, that is, whether to ignore bind parameters that do not have corresponding fields in the target object. <p> Default is "true". Turn this off to enforce that
     * all bind parameters must have a matching field in the target object.
     *
     * @param ignoreUnknownFields if unknown fields should be ignored
     */
    public void setIgnoreUnknownFields(boolean ignoreUnknownFields) {
        this.ignoreUnknownFields = ignoreUnknownFields;
    }

    /**
     * Set whether to ignore invalid fields, that is, whether to ignore bind parameters that have corresponding fields in the target object which are not accessible (for example because of null values
     * in the nested path). <p> Default is "false". Turn this on to ignore bind parameters for nested objects in non-existing parts of the target object graph.
     *
     * @param ignoreInvalidFields if invalid fields should be ignored
     */
    public void setIgnoreInvalidFields(boolean ignoreInvalidFields) {
        this.ignoreInvalidFields = ignoreInvalidFields;
    }

    /**
     * Set the target name.
     *
     * @param targetName the target name
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    /**
     * Flag to indicate that placeholders should be replaced during binding. Default is true.
     *
     * @param resolvePlaceholders flag value
     */
    public void setResolvePlaceholders(boolean resolvePlaceholders) {
        this.resolvePlaceholders = resolvePlaceholders;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        bindPropertiesToTarget();
    }

    @Override
    public Class<?> getObjectType() {
        if (this.target == null) {
            return Object.class;
        }
        return this.target.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public T getObject() throws Exception {
        if (!this.hasBeenBound) {
            bindPropertiesToTarget();
        }
        return this.target;
    }

    void bindPropertiesToTarget() throws BindException {
        Assert.state(this.propertySources != null, "PropertySources should not be null");
        try {
            if (logger.isTraceEnabled()) {
                logger.trace("Property Sources: " + this.propertySources);

            }
            this.hasBeenBound = true;
            doBindPropertiesToTarget();
        } catch (BindException ex) {
            if (this.exceptionIfInvalid) {
                throw ex;
            }
            logger.error("Failed to load Properties validation bean. "
                         + "Your Properties may be invalid.", ex);
        }
    }

    private void doBindPropertiesToTarget() throws BindException {
        RelaxedDataBinder dataBinder = (this.targetName != null)
                                        ? new RelaxedDataBinder(this.target, this.targetName)
                                        : new RelaxedDataBinder(this.target);

        if (this.conversionService != null) {
            dataBinder.setConversionService(this.conversionService);
        }
        dataBinder.setAutoGrowCollectionLimit(Integer.MAX_VALUE);
        dataBinder.setIgnoreNestedProperties(this.ignoreNestedProperties);
        dataBinder.setIgnoreInvalidFields(this.ignoreInvalidFields);
        dataBinder.setIgnoreUnknownFields(this.ignoreUnknownFields);

        PropertyValues propertyValues = new PropertySourcesPropertyValues(propertySources, true);
        dataBinder.bind(propertyValues);
    }
}

