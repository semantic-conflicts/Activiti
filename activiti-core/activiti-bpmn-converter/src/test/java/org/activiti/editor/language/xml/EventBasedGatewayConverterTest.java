package org.activiti.editor.language.xml;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.activiti.bpmn.model.ActivitiListener;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EventGateway;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.ImplementationType;
import org.junit.jupiter.api.Test;

/**
 * Test for ACT-1657
 */
public class EventBasedGatewayConverterTest extends AbstractConverterTest {

    @Test
    public void convertXMLToModel() throws Exception {
        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
    }

    protected String getResource() {
        return "eventgatewaymodel.bpmn";
    }

    private void validateModel(BpmnModel model) {
        FlowElement flowElement = model.getMainProcess().getFlowElement("eventBasedGateway");
        assertNotNull(flowElement);
        assertThat(flowElement).isInstanceOf(EventGateway.class);

        EventGateway gateway = (EventGateway) flowElement;
        List<ActivitiListener> listeners = gateway.getExecutionListeners();
        assertEquals(1, listeners.size());
        ActivitiListener listener = listeners.get(0);
        assertThat(listener.getImplementationType()).isEqualTo(ImplementationType.IMPLEMENTATION_TYPE_CLASS);
        assertEquals("org.test.TestClass", listener.getImplementation());
        assertEquals("start", listener.getEvent());
    }

}
