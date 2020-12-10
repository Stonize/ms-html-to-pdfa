package uk.gov.dwp.pdfa.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import uk.gov.dwp.pdf.exception.PdfaGeneratorException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class JsonPdfTemplateInputItem {

  // @JsonProperty("font_map")
  private Map<String, String> fontMap;

  // @JsonProperty("colour_profile")
  private String colourProfile;

  // @JsonProperty("page_html")
  private String htmlDocument;

  // @JsonProperty("conformance_level")
  private String conformanceLevel;

  private JsonNode inputs;

  public String getHtmlDocument() {
    return htmlDocument;
  }

  public void setFontMap(Map<String, String> fontMap) {
    this.fontMap = fontMap;
  }

  public void setColourProfile(String colourProfile) {
    this.colourProfile = colourProfile;
  }

  public void setHtmlDocument(String htmlDocument) {
    this.htmlDocument = htmlDocument;
  }

  public void setConformanceLevel(String conformanceLevel) {
    this.conformanceLevel = conformanceLevel;
  }

  public void setInputs(JsonNode inputs) {
    this.inputs = inputs;
  }

  /*
  private String getOrNull(JsonNode jsonNode, String key) {
    JsonNode node = jsonNode.get(key);
    if (node == null) {
      return null;
    }
    return node.toString();
  }

  public JsonPdfTemplateInputItem(String json) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
    JsonNode fontMap = jsonNode.get("font_map");
    if (fontMap != null) {
      this.fontMap = mapper.convertValue(fontMap, Map.class);
    }
    this.colourProfile = getOrNull(jsonNode, "colour_profile");
    this.htmlDocument = getOrNull(jsonNode, "page_html");
    this.conformanceLevel = getOrNull(jsonNode, "conformance_level");
    this.inputs = jsonNode.get("inputs");
  }
   */

  public JsonNode getInputs() {
    return inputs;
  }

  public Map<String, byte[]> getFontMap() throws PdfaGeneratorException {
    Map<String, byte[]> outputMap = null;
    try {

      if (fontMap != null) {
        outputMap = new HashMap<>();

        for (Map.Entry<String, String> item : fontMap.entrySet()) {
          outputMap.put(item.getKey(), Base64.getDecoder().decode(item.getValue()));
        }
      }

    } catch (IllegalArgumentException e) {
      throw new PdfaGeneratorException(
          String.format("'font_map' elements are malformed :: %s", e.getMessage()));
    }

    return outputMap;
  }

  public byte[] getColourProfile() throws PdfaGeneratorException {
    byte[] colour;
    try {
      colour = colourProfile != null ? Base64.getDecoder().decode(colourProfile) : null;

    } catch (IllegalArgumentException e) {
      throw new PdfaGeneratorException(
          String.format("'colour_profile' element is malformed :: %s", e.getMessage()));
    }

    return colour;
  }

  /*
  public String getHtmlDocument() throws PdfaGeneratorException {
    String html;
    try {
      ClassLoader classLoader = getClass().getClassLoader();
      URL resource = classLoader.getResource("templates/" + htmlDocument + ".html");
      html = htmlDocument != null ? Files.readString(Paths.get(resource.toURI())) : null;
    } catch (IllegalArgumentException | URISyntaxException | IOException e) {
      throw new PdfaGeneratorException(
          String.format("'page_html' element is malformed :: %s", e.getMessage()));
    }
    return html;
  }
  */

  public String getConformanceLevel() {
    return this.conformanceLevel;
  }
}
