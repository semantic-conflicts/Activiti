package org.activiti.editor.language.xml;

import org.activiti.bpmn.exceptions.XMLException;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.MapExceptionEntry;
import org.activiti.bpmn.model.ServiceTask;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class MapExceptionConverterTest extends AbstractConverterTest {

    String resourceName;

    protected String getResource() {
        return resourceName;
    }

    @Test
    public void testMapExceptionWithInvalidHasChildren() {
        resourceName = "mapException/mapExceptionInvalidHasChildrenModel.bpmn";

        try {
            readXMLFile();
            fail("No exception is thrown for mapExecution with invalid boolean for hasChildren");
        } catch (XMLException x) {
            assertThat(x.getMessage()).contains("is not valid boolean");
        } catch (Exception e) {
            fail("wrong exception thrown. XmlException expected, " + e.getClass() + " thrown");
        }
    }

    @Test
    public void testMapExceptionWithNoErrorCode() {
        resourceName = "mapException/mapExceptionNoErrorCode.bpmn";
        try {
            readXMLFile();
            fail("No exception is thrown for mapExecution with no Error Code");
        } catch (XMLException x) {
            assertThat(x.getMessage()).contains("No errorCode defined");
        } catch (Exception e) {
            fail("wrong exception thrown. XmlException expected, " + e.getClass() + " thrown");
        }
    }

    @Test
    public void testMapExceptionWithNoExceptionClass() throws Exception {
        resourceName = "mapException/mapExceptionNoExceptionClass.bpmn";

        BpmnModel bpmnModel = readXMLFile();
        FlowElement flowElement = bpmnModel.getMainProcess().getFlowElement("servicetaskWithAndTrueAndChildren");
        assertThat(flowElement).isInstanceOf(ServiceTask.class);
        assertThat(flowElement.getId()).isEqualTo("servicetaskWithAndTrueAndChildren");
        ServiceTask serviceTask = (ServiceTask) flowElement;
        assertThat(serviceTask.getMapExceptions()).hasSize(1);
        assertThat(serviceTask.getMapExceptions().get(0).getClassName()).hasSize(0);
    }

    @Test
    public void convertXMLToModel() throws Exception {
        resourceName = "mapException/mapExceptionModel.bpmn";

        BpmnModel bpmnModel = readXMLFile();
        validateModel(bpmnModel);
    }

    private void validateModel(BpmnModel model) {

        // check service task with andChildren Set to True
        FlowElement flowElement = model.getMainProcess().getFlowElement("servicetaskWithAndTrueAndChildren");
        assertThat(flowElement).isInstanceOf(ServiceTask.class);
        assertThat(flowElement.getId()).isEqualTo("servicetaskWithAndTrueAndChildren");
        ServiceTask serviceTask = (ServiceTask) flowElement;
        assertThat(serviceTask.getMapExceptions()).hasSize(3);

        // check a normal mapException, with hasChildren == true
        MapExceptionEntry mapExceptionEntryWithHasChildrenTrue = serviceTask.getMapExceptions().get(0);
        assertThat(mapExceptionEntryWithHasChildrenTrue.getErrorCode()).isEqualTo("myErrorCode1");
        assertThat(mapExceptionEntryWithHasChildrenTrue.getClassName()).isEqualTo("com.activiti.Something1");
        assertThat(mapExceptionEntryWithHasChildrenTrue.isAndChildren()).isTrue();

        // check a normal mapException, with hasChildren == false
        MapExceptionEntry mapExceptionEntryWithHasChildrenFalse = serviceTask.getMapExceptions().get(1);
        assertThat(mapExceptionEntryWithHasChildrenFalse.getErrorCode()).isEqualTo("myErrorCode2");
        assertThat(mapExceptionEntryWithHasChildrenFalse.getClassName()).isEqualTo("com.activiti.Something2");
        assertThat(mapExceptionEntryWithHasChildrenFalse.isAndChildren()).isFalse();

        // check a normal mapException, with no hasChildren Defined, default should be false
        MapExceptionEntry mapExceptionEntryWithHasChildrenDefault = serviceTask.getMapExceptions().get(2);
        assertThat(mapExceptionEntryWithHasChildrenFalse.getErrorCode()).isEqualTo("myErrorCode3");
        assertThat(mapExceptionEntryWithHasChildrenFalse.getClassName()).isEqualTo("com.activiti.Something3");
        assertThat(mapExceptionEntryWithHasChildrenFalse.isAndChildren()).isFalse();

        // if no map exception is defined, getMapException should return a not
        // null
        // empty list
        FlowElement flowElement1 = model.getMainProcess().getFlowElement("servicetaskWithNoMapException");
        assertThat(flowElement1).isInstanceOf(ServiceTask.class);
        assertThat(flowElement1.getId()).isEqualTo("servicetaskWithNoMapException");

        ServiceTask serviceTask1 = (ServiceTask) flowElement1;
        assertThat(serviceTask1.getMapExceptions()).hasSize(0);
    }

}
