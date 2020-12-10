package uk.gov.dwp.pdfa.template;

import com.fasterxml.jackson.databind.JsonNode;
import freemarker.template.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

public class TemplateEngine {

    public static String getTemplate(String path, String template, JsonNode jsonInput) throws IOException, TemplateException {

        // Create template
        Configuration cfg = new Configuration(new Version(2, 3, 20));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLocale(Locale.ITALY);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Process template
        cfg.setTemplateLoader(new SingleTemplateLoader(path, template, jsonInput));
        Template fmTemplate = cfg.getTemplate("whatever");

        // Generate the output
        StringWriter writer = new StringWriter();
        fmTemplate.process(null, writer);
        return writer.toString();

    }

}
