package org.activiti.editor.language.xml;

import static org.junit.jupiter.api.Assertions.fail;

import org.activiti.bpmn.exceptions.XMLException;
import org.junit.jupiter.api.Test;

public class EmptyModelTest extends AbstractConverterTest {

  @Test
  public void convertXMLToModel() throws Exception {
    try {
      readXMLFile();
      fail("Expected xml exception");
    } catch (XMLException e) {
      // exception expected
    }
  }

  @Test
  public void convertModelToXML() throws Exception {
    try {
      readXMLFile();
      fail("Expected xml exception");
    } catch (XMLException e) {
      // exception expected
    }
  }

  protected String getResource() {
    return "empty.bpmn";
  }
}
