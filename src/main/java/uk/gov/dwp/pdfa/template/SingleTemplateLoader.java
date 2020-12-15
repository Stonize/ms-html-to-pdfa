package uk.gov.dwp.pdfa.template;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.cache.TemplateLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Return a single template with a fixed input injected directly in the template.
 * Json payload must be single line
 */
public class SingleTemplateLoader implements TemplateLoader {

    private String path;

    private JsonNode json;

    private String template;

    public SingleTemplateLoader(String path, String template, JsonNode json) {
        this.path = path;
        this.json = json;
        this.template = template;
    }

    @Override
    public Object findTemplateSource(java.lang.String s) throws IOException {
        return Files.readString(Path.of(this.path).resolve(this.template));
    }

    @Override
    public long getLastModified(Object o) {
        return 0;
    }

    @Override
    public Reader getReader(Object o, java.lang.String s) throws IOException {
        StringBuilder buffer = new StringBuilder();
        if (this.json != null) {
            buffer.append("<#assign inputs = ");
            buffer.append(this.json.toString());
            buffer.append(">\n");
        }
        buffer.append(o);
        return new StringReader(buffer.toString());
    }

    @Override
    public void closeTemplateSource(Object o) throws IOException {

    }
}
