package org.activiti.engine.impl.persistence.entity;
import java.util.List;
import java.util.Map;
import org.activiti.engine.impl.DeploymentQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.ProcessDefinitionQueryImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.event.MessageEventHandler;
import org.activiti.engine.impl.jobexecutor.TimerStartEventJobHandler;
import org.activiti.engine.impl.persistence.AbstractManager;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Job;
/** 
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class DeploymentEntityManager extends AbstractManager {
  public void insertDeployment(  DeploymentEntity deployment){
    getDbSqlSession().insert(deployment);
    for (    ResourceEntity resource : deployment.getResources().values()) {
      resource.setDeploymentId(deployment.getId());
      getResourceManager().insertResource(resource);
    }
  }
  public void deleteDeployment(  String deploymentId,  boolean cascade){
    List<ProcessDefinition> processDefinitions=getDbSqlSession().createProcessDefinitionQuery().deploymentId(deploymentId).list();
    List<Model> models=getDbSqlSession().createModelQueryImpl().deploymentId(deploymentId).list();
    for (    Model model : models) {
      ModelEntity modelEntity=(ModelEntity)model;
      modelEntity.setDeploymentId(null);
      getModelManager().updateModel(modelEntity);
    }
    if (cascade) {
      for (      ProcessDefinition processDefinition : processDefinitions) {
        String processDefinitionId=processDefinition.getId();
        getProcessInstanceManager().deleteProcessInstancesByProcessDefinition(processDefinitionId,"deleted deployment",cascade);
      }
    }
    for (    ProcessDefinition processDefinition : processDefinitions) {
      String processDefinitionId=processDefinition.getId();
      getIdentityLinkManager().deleteIdentityLinksByProcDef(processDefinitionId);
      getEventSubscriptionManager().deleteEventSubscriptionsForProcessDefinition(processDefinitionId);
    }
    getProcessDefinitionManager().deleteProcessDefinitionsByDeploymentId(deploymentId);
    for (    ProcessDefinition processDefinition : processDefinitions) {
      List<Job> timerStartJobs=Context.getCommandContext().getJobEntityManager().findJobsByConfiguration(TimerStartEventJobHandler.TYPE,processDefinition.getKey());
      if (timerStartJobs != null && timerStartJobs.size() > 0) {
        long nrOfVersions=new ProcessDefinitionQueryImpl(Context.getCommandContext()).processDefinitionKey(processDefinition.getKey()).count();
        long nrOfProcessDefinitionsWithSameKey=0;
        for (        ProcessDefinition p : processDefinitions) {
          if (!p.getId().equals(processDefinition.getId()) && p.getKey().equals(processDefinition.getKey())) {
            nrOfProcessDefinitionsWithSameKey++;
          }
        }
        if (nrOfVersions - nrOfProcessDefinitionsWithSameKey <= 1) {
          for (          Job job : timerStartJobs) {
            ((JobEntity)job).delete();
          }
        }
      }
      List<EventSubscriptionEntity> findEventSubscriptionsByConfiguration=Context.getCommandContext().getEventSubscriptionEntityManager().findEventSubscriptionsByConfiguration(MessageEventHandler.EVENT_HANDLER_TYPE,processDefinition.getId(),processDefinition.getTenantId());
      for (      EventSubscriptionEntity eventSubscriptionEntity : findEventSubscriptionsByConfiguration) {
        eventSubscriptionEntity.delete();
      }
    }
    getResourceManager().deleteResourcesByDeploymentId(deploymentId);
    getDbSqlSession().delete("deleteDeployment",deploymentId);
  }
  public DeploymentEntity findLatestDeploymentByName(  String deploymentName){
    List<?> list=getDbSqlSession().selectList("selectDeploymentsByName",deploymentName,0,1);
    if (list != null && !list.isEmpty()) {
      return (DeploymentEntity)list.get(0);
    }
    return null;
  }
  public DeploymentEntity findDeploymentById(  String deploymentId){
    return (DeploymentEntity)getDbSqlSession().selectOne("selectDeploymentById",deploymentId);
  }
  public long findDeploymentCountByQueryCriteria(  DeploymentQueryImpl deploymentQuery){
    return (Long)getDbSqlSession().selectOne("selectDeploymentCountByQueryCriteria",deploymentQuery);
  }
  @SuppressWarnings("unchecked") public List<Deployment> findDeploymentsByQueryCriteria(  DeploymentQueryImpl deploymentQuery,  Page page){
    final String query="selectDeploymentsByQueryCriteria";
    return getDbSqlSession().selectList(query,deploymentQuery,page);
  }
  public List<String> getDeploymentResourceNames(  String deploymentId){
    return getDbSqlSession().getSqlSession().selectList("selectResourceNamesByDeploymentId",deploymentId);
  }
  @SuppressWarnings("unchecked") public List<Deployment> findDeploymentsByNativeQuery(  Map<String,Object> parameterMap,  int firstResult,  int maxResults){
    return getDbSqlSession().selectListWithRawParameter("selectDeploymentByNativeQuery",parameterMap,firstResult,maxResults);
  }
  public long findDeploymentCountByNativeQuery(  Map<String,Object> parameterMap){
    return (Long)getDbSqlSession().selectOne("selectDeploymentCountByNativeQuery",parameterMap);
  }
  public void close(){
  }
  public void flush(){
  }
  public DeploymentEntityManager(){
  }
}
