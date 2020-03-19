package org.activiti.editor.language.xml;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.UserTask;
import org.junit.jupiter.api.Test;

public class EncodingConverterTest extends AbstractConverterTest {

    @Test
    public void convertXMLToModel() throws Exception {
        BpmnModel bpmnModel = readXMLFileEncoding(StandardCharsets.ISO_8859_1.name());
        validateModel(bpmnModel);
    }

    @Test
    public void convertModelToXML() throws Exception {
        BpmnModel bpmnModel = readXMLFileEncoding(StandardCharsets.ISO_8859_1.name());
        BpmnModel parsedModel = exportAndReadXMLFile(bpmnModel);
        validateModel(parsedModel);
    }

    private void validateModel(BpmnModel model) {
        FlowElement flowElement = model.getMainProcess().getFlowElement("writeReportTask");
        assertNotNull(flowElement);
        assertThat(flowElement).isInstanceOf(UserTask.class);
        assertEquals("writeReportTask", flowElement.getId());
        UserTask userTask = (UserTask) flowElement;
        assertEquals("writeReportTask", userTask.getId());
        assertEquals("Fazer relatório", userTask.getName());
    }

    protected String getResource() {
        return "encoding.bpmn";
    }
}
