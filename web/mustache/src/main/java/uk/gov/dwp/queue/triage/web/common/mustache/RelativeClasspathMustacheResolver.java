package uk.gov.dwp.queue.triage.web.common.mustache;

import com.github.mustachejava.MustacheResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.web.common.Page;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class RelativeClasspathMustacheResolver implements MustacheResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelativeClasspathMustacheResolver.class);
    private final Class<? extends Page> klass;

    public RelativeClasspathMustacheResolver(Class<? extends Page> klass) {
        this.klass = klass;
    }

    @Override
    public Reader getReader(String resourceName) {
        final InputStream is = klass.getResourceAsStream(resourceName);
        if (is == null) {
            LOGGER.warn("Could not find: '{}' relative to Page: {}", resourceName, klass.getName());
            return null;
        }
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    }
}
