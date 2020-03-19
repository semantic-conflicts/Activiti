package org.activiti.editor.language.xml;

import static org.assertj.core.api.Assertions.*;

import org.activiti.bpmn.model.BoundaryEvent;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.IntermediateCatchEvent;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.bpmn.model.TimerEventDefinition;
import org.junit.jupiter.api.Test;

public class TimerDefinitionConverterTest extends AbstractConverterTest {

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
        return "timerCalendarDefinition.bpmn";
    }

    private void validateModel(BpmnModel model) {
        IntermediateCatchEvent timer = (IntermediateCatchEvent) model.getMainProcess().getFlowElement("timer");
        assertThat(timer).isNotNull();
        TimerEventDefinition timerEvent = (TimerEventDefinition) timer.getEventDefinitions().get(0);
        assertThat(timerEvent.getCalendarName()).isEqualTo("custom");
        assertThat("PT5M").isEqualTo(timerEvent.getTimeDuration());

        StartEvent start = (StartEvent) model.getMainProcess().getFlowElement("theStart");
        assertThat(start).isNotNull();
        TimerEventDefinition startTimerEvent = (TimerEventDefinition) start.getEventDefinitions().get(0);
        assertThat(startTimerEvent.getCalendarName()).isEqualTo("custom");
        assertThat("R2/PT5S").isEqualTo(startTimerEvent.getTimeCycle());
        assertThat("${EndDate}").isEqualTo(startTimerEvent.getEndDate());

        BoundaryEvent boundaryTimer = (BoundaryEvent) model.getMainProcess().getFlowElement("boundaryTimer");
        assertThat(boundaryTimer).isNotNull();
        TimerEventDefinition boundaryTimerEvent = (TimerEventDefinition) boundaryTimer.getEventDefinitions().get(0);
        assertThat(boundaryTimerEvent.getCalendarName()).isEqualTo("custom");
        assertThat("PT10S").isEqualTo(boundaryTimerEvent.getTimeDuration());
        assertThat(boundaryTimerEvent.getEndDate()).isNull();
    }

}
