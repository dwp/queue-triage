package uk.gov.dwp.queue.triage.web.common.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import org.junit.Test;
import uk.gov.dwp.queue.triage.web.common.Page;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class MustachePageRendererTest {

    private final MustachePageRenderer underTest = new MustachePageRenderer(new DefaultMustacheFactory("./mustache"));

    @Test
    public void testRenderPage() throws Exception {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            underTest.render(new TestPage(), output);
            assertThat(output.toString(), containsString("<p>some content</p>"));
        }
    }

    public static class TestPage extends Page {

        public TestPage() {
            super("testPage.mustache");
        }

        public String getContent() {
            return "some content";
        }
    }

}