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

  private Map<String, String> fontMap;

  private String colourProfile;

  private String htmlDocument;

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

  public String getConformanceLevel() {
    return this.conformanceLevel;
  }
}
