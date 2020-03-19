package org.activiti.editor.language.xml;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.ImplementationType;
import org.activiti.bpmn.model.SequenceFlow;
import org.junit.jupiter.api.Test;

public class AsyncEndEventConverterTest extends AbstractConverterTest {

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
        return "asyncendeventmodel.bpmn";
    }

    private void validateModel(BpmnModel model) {
        FlowElement flowElement = model.getMainProcess().getFlowElement("endEvent");
        assertThat(flowElement).isNotNull();
        assertThat(flowElement).isInstanceOf(EndEvent.class);
        assertEquals("endEvent", flowElement.getId());
        EndEvent endEvent = (EndEvent) flowElement;
        assertEquals("endEvent", endEvent.getId());
        assertThat(endEvent.isAsynchronous()).isTrue();

        List<ActivitiListener> listeners = endEvent.getExecutionListeners();
        assertEquals(1, listeners.size());
        ActivitiListener listener = listeners.get(0);
        assertThat(ImplementationType.IMPLEMENTATION_TYPE_CLASS.equals(listener.getImplementationType())).isTrue();
        assertEquals("org.test.TestClass", listener.getImplementation());
        assertEquals("start", listener.getEvent());

        assertEquals(1, endEvent.getIncomingFlows().size());
        SequenceFlow sequence = endEvent.getIncomingFlows().get(0);
        assertEquals("sid-91C0F3A0-649F-462E-A1C1-1CE499FEDE3E", sequence.getId());
    }

}
