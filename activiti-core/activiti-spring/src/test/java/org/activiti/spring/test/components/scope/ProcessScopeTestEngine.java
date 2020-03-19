package org.activiti.spring.test.components.scope;

import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

class ProcessScopeTestEngine {

    private int customerId = 43;

    private String keyForObjectType(Map<String, Object> runtimeVars, Class<?> clazz) {
        for (Map.Entry<String, Object> e : runtimeVars.entrySet()) {
            Object value = e.getValue();
            if (value.getClass().isAssignableFrom(clazz)) {
                return e.getKey();
            }
        }
        return null;
    }

    private StatefulObject run() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("customerId", customerId);

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("component-waiter", vars);

        Map<String, Object> runtimeVars = runtimeService.getVariables(processInstance.getId());

        String statefulObjectVariableKey = keyForObjectType(runtimeVars, StatefulObject.class);

        assertThat(runtimeVars).isNotEmpty();
        assertThat(statefulObjectVariableKey).isNotBlank();

        StatefulObject scopedObject = (StatefulObject) runtimeService.getVariable(processInstance.getId(), statefulObjectVariableKey);
        assertThat(scopedObject).isNotNull();
        assertThat(scopedObject.getName()).isNotBlank();
        assertThat(scopedObject.getVisitedCount()).isEqualTo(2);

        // the process has paused
        String procId = processInstance.getProcessInstanceId();

        List<Task> tasks = taskService.createTaskQuery().executionId(procId).list();
        assertThat(tasks).hasSize(1);

        Task t = tasks.iterator().next();
        this.taskService.claim(t.getId(), "me");
        this.taskService.complete(t.getId());

        scopedObject = (StatefulObject) runtimeService.getVariable(processInstance.getId(), statefulObjectVariableKey);
        assertThat(scopedObject.getVisitedCount()).isEqualTo(3);

        assertThat(scopedObject.getCustomerId()).isEqualTo(customerId);
        return scopedObject;
    }

    private ProcessEngine processEngine;
    private RuntimeService runtimeService;
    private TaskService taskService;

    public void testScopedProxyCreation() {

        StatefulObject one = run();
        StatefulObject two = run();
        assertThat(one.getName()).isNotSameAs(two.getName());
        assertThat(one.getVisitedCount()).isEqualTo(two.getVisitedCount());
    }

    public ProcessScopeTestEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
        this.runtimeService = this.processEngine.getRuntimeService();
        this.taskService = this.processEngine.getTaskService();
    }

}
