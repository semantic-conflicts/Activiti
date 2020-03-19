package org.activiti.editor.language.xml;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.DataStore;
import org.activiti.bpmn.model.DataStoreReference;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Pool;
import org.junit.jupiter.api.Test;

public class DataStoreConverterTest extends AbstractConverterTest {

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
    }

    protected String getResource() {
        return "datastore.bpmn";
    }

    private void validateModel(BpmnModel model) {
        assertEquals(1, model.getDataStores().size());
        DataStore dataStore = model.getDataStore("DataStore_1");
        assertNotNull(dataStore);
        assertEquals("DataStore_1", dataStore.getId());
        assertEquals("test", dataStore.getDataState());
        assertEquals("Test Database", dataStore.getName());
        assertEquals("test", dataStore.getItemSubjectRef());

        FlowElement refElement = model.getFlowElement("DataStoreReference_1");
        assertNotNull(refElement);
        assertThat(refElement).isInstanceOf(DataStoreReference.class);

        assertEquals(1, model.getPools().size());
        Pool pool = model.getPools().get(0);
        assertEquals("pool1", pool.getId());
    }
}
