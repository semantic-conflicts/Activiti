package org.activiti.editor.language.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Lane;
import org.activiti.bpmn.model.Pool;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SequenceFlow;
import org.junit.jupiter.api.Test;

public class PoolsConverterTest extends AbstractConverterTest {

  @Test
  public void convertXMLToModel() throws Exception {
    BpmnModel bpmnModel = readXMLFile();
    validateModel(bpmnModel);
  }

  @Test
  public void convertModelToXML() throws Exception {
    BpmnModel bpmnModel = readXMLFile();
    BpmnModel parsedModel = exportAndReadXMLFile(bpmnModel);
    validateModel(parsedModel);
    deployProcess(parsedModel);
  }

  protected String getResource() {
    return "pools.bpmn";
  }

  private void validateModel(BpmnModel model) {
    assertEquals(1, model.getPools().size());
    Pool pool = model.getPools().get(0);
    assertEquals("pool1", pool.getId());
    assertEquals("Pool", pool.getName());
    Process process = model.getProcess(pool.getId());
    assertNotNull(process);
    assertEquals(2, process.getLanes().size());
    Lane lane = process.getLanes().get(0);
    assertEquals("lane1", lane.getId());
    assertEquals("Lane 1", lane.getName());
    assertEquals(2, lane.getFlowReferences().size());
    lane = process.getLanes().get(1);
    assertEquals("lane2", lane.getId());
    assertEquals("Lane 2", lane.getName());
    assertEquals(2, lane.getFlowReferences().size());
    FlowElement flowElement = process.getFlowElement("flow1");
    assertNotNull(flowElement);
    assertTrue(flowElement instanceof SequenceFlow);
  }
}
