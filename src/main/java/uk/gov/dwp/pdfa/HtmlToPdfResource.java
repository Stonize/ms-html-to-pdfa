package uk.gov.dwp.pdfa;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.pdf.generator.HtmlToPdfGenerator;
import uk.gov.dwp.pdf.generator.PdfConformanceLevel;
import uk.gov.dwp.pdfa.items.JsonPdfInputItem;
import uk.gov.dwp.pdfa.items.JsonPdfTemplateInputItem;
import uk.gov.dwp.pdfa.template.TemplateEngine;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class HtmlToPdfResource {

  private static final Logger LOGGER = LoggerFactory.getLogger(HtmlToPdfResource.class.getName());
  private final Map<String, byte[]> defaultArialFont = new HashMap<>();
  private final HtmlToPdfGenerator pdfGenerator;
  private final byte[] defaultColourProfile;
  private final String templateRootDirectory;

  public HtmlToPdfResource(HtmlToPdfGenerator pdfGenerator) throws IOException {
    this.getDefaultArialFont().put("courier", IOUtils.toByteArray(getClass().getResourceAsStream("/fonts/courier.ttf")));
    this.getDefaultArialFont().put("arial", IOUtils.toByteArray(getClass().getResourceAsStream("/fonts/arial.ttf")));
    this.defaultColourProfile = IOUtils.toByteArray(getClass().getResourceAsStream("/colours/sRGB.icm"));
    this.pdfGenerator = pdfGenerator;
    if (System.getenv("TEMPLATES_DIR") != null) {
      this.templateRootDirectory = System.getenv("TEMPLATES_DIR");
    } else {
      this.templateRootDirectory = "/usr/local/templates";
    }
  }

  private String getOrNull(JsonNode jsonNode, String key) {
    JsonNode node = jsonNode.get(key);
    if (node == null) {
      return null;
    }
    return node.asText();
  }

  private JsonPdfTemplateInputItem parseTemplateInput(String json) throws JsonProcessingException {
    JsonPdfTemplateInputItem template = new JsonPdfTemplateInputItem();
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
    JsonNode fontMap = jsonNode.get("font_map");
    if (fontMap != null) {
      template.setFontMap( mapper.convertValue(fontMap, Map.class));
    }
    template.setColourProfile(this.getOrNull(jsonNode, "colour_profile"));
    template.setHtmlDocument(this.getOrNull(jsonNode, "page_html"));
    template.setConformanceLevel(this.getOrNull(jsonNode, "conformance_level"));
    template.setInputs(jsonNode.get("inputs"));
    return template;
  }

  @POST()
  @Path("/generatePdfFromTemplate")
  public Response generatePdfDocumentFromTemplate(String json) {
    Response response;

    try {

      JsonPdfTemplateInputItem pdfInputItem = parseTemplateInput(json);
      LOGGER.info("successfully de-serialised input json");

      String template = TemplateEngine.getTemplate(this.templateRootDirectory, pdfInputItem.getHtmlDocument(), pdfInputItem.getInputs());

      byte[] pdf =
              pdfGenerator.createPdfDocument(
                      template,
                      pdfInputItem.getColourProfile() != null
                              ? pdfInputItem.getColourProfile()
                              : getDefaultColourProfile(),
                      pdfInputItem.getFontMap() != null && pdfInputItem.getFontMap().size() > 0
                              ? pdfInputItem.getFontMap()
                              : getDefaultArialFont(),
                      pdfInputItem.getConformanceLevel() != null
                              ? PdfConformanceLevel.valueOf(pdfInputItem.getConformanceLevel())
                              : PdfConformanceLevel.PDF_UA);

      response = Response.status(HttpStatus.SC_OK).type("application/pdf").entity(pdf).build();

      LOGGER.info("successfully written encoded pdf to response");

    } catch (JsonParseException | JsonMappingException e) {
      response =
              Response.status(HttpStatus.SC_BAD_REQUEST)
                      .entity(String.format("Json formatting exception :: %s", e.getMessage()))
                      .build();
      LOGGER.debug(e.getClass().getName(), e);
      LOGGER.error(e.getMessage());

    } catch (IOException e) {
      response =
              Response.status(HttpStatus.SC_BAD_REQUEST)
                      .entity(String.format("IOException :: %s", e.getMessage()))
                      .build();
      LOGGER.debug(e.getClass().getName(), e);
      LOGGER.error(e.getMessage());

    } catch (Exception e) {
      response =
              Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                      .entity(String.format("%s :: %s", e.getClass().getName(), e.getMessage()))
                      .build();
      LOGGER.debug(e.getClass().getName(), e);
      LOGGER.error(e.getMessage());
    }

    return response;

  }

  @POST
  @Path("/generatePdf")
  public Response generatePdfDocument(String json) {
    Response response;

    try {

      JsonPdfInputItem pdfInputItem = new ObjectMapper().readValue(json, JsonPdfInputItem.class);
      LOGGER.info("successfully serialised input json");

      byte[] pdf =
          pdfGenerator.createPdfDocument(
              pdfInputItem.getHtmlDocument(),
              pdfInputItem.getColourProfile() != null
                  ? pdfInputItem.getColourProfile()
                  : getDefaultColourProfile(),
              pdfInputItem.getFontMap() != null && pdfInputItem.getFontMap().size() > 0
                  ? pdfInputItem.getFontMap()
                  : getDefaultArialFont(),
              pdfInputItem.getConformanceLevel() != null
                  ? PdfConformanceLevel.valueOf(pdfInputItem.getConformanceLevel())
                  : PdfConformanceLevel.PDF_UA);

      response =
          Response.status(HttpStatus.SC_OK).entity(Base64.getEncoder().encodeToString(pdf)).build();
      LOGGER.info("successfully written encoded pdf to response");

    } catch (JsonParseException | JsonMappingException e) {
      response =
          Response.status(HttpStatus.SC_BAD_REQUEST)
              .entity(String.format("Json formatting exception :: %s", e.getMessage()))
              .build();
      LOGGER.debug(e.getClass().getName(), e);
      LOGGER.error(e.getMessage());

    } catch (IOException e) {
      response =
          Response.status(HttpStatus.SC_BAD_REQUEST)
              .entity(String.format("IOException :: %s", e.getMessage()))
              .build();
      LOGGER.debug(e.getClass().getName(), e);
      LOGGER.error(e.getMessage());

    } catch (Exception e) {
      response =
          Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
              .entity(String.format("%s :: %s", e.getClass().getName(), e.getMessage()))
              .build();
      LOGGER.debug(e.getClass().getName(), e);
      LOGGER.error(e.getMessage());
    }

    return response;
  }

  private Map<String, byte[]> getDefaultArialFont() {
    return defaultArialFont;
  }

  private byte[] getDefaultColourProfile() {
    return defaultColourProfile;
  }
}
