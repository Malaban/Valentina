package com.java.activiti.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.java.activiti.model.PageInfo;
import com.java.activiti.service.LeaveService;
import com.java.activiti.util.ResponseUtil;

/**
 * 流程定义管理
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/processDefinition")
public class ProcessDefinitionController {

	@Resource
	private RepositoryService repositoryService;

	@Resource
	private HistoryService historyService;
	@Resource
	private TaskService taskService;
	@Resource
	private RuntimeService runtimeService;
	@Autowired
	ProcessEngineConfiguration processEngineConfiguration;
	@Autowired
	ProcessEngineFactoryBean processEngine;
	@Resource
	private LeaveService leaveService;

	/**
	 * 流程定义分页查询
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/processDefinitionPage")
	public String processDefinitionPage(String rows, String s_name, String page, HttpServletResponse response)
			throws Exception {
		if (s_name == null) {
			s_name = "";
		}
		PageInfo pageInfo = new PageInfo();
		Integer sizePage = Integer.parseInt(rows);
		pageInfo.setPageSize(sizePage);
		if (page == null || page.equals("")) {
			page = "1";
		}
		pageInfo.setPageIndex((Integer.parseInt(page) - 1) * sizePage);
		long count = repositoryService.createProcessDefinitionQuery().processDefinitionNameLike("%" + s_name + "%")
				.count();
		List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery()
				.orderByDeploymentId().desc().processDefinitionNameLike("%" + s_name + "%")
				.listPage(pageInfo.getPageIndex(), pageInfo.getPageSize());
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "identityLinks", "processDefinition" });
		JSONObject result = new JSONObject();
		JSONArray jsonArray = JSONArray.fromObject(processDefinitionList, jsonConfig);
		result.put("rows", jsonArray);
		result.put("total", count);
		ResponseUtil.write(response, result);
		return null;
	}

	/**
	 * 查看流程图
	 * 
	 * @param deploymentId
	 *            流程ID
	 * @param diagramResourceName
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/showView")
	public String showView(String deploymentId, String diagramResourceName, HttpServletResponse response)
			throws Exception {
		InputStream inputStream = repositoryService.getResourceAsStream(deploymentId, diagramResourceName);
		OutputStream out = response.getOutputStream();
		for (int b = -1; (b = inputStream.read()) != -1;) {
			out.write(b);
		}
		out.close();
		inputStream.close();
		return null;
	}

	/**
	 * 查看流程图
	 * 
	 * @param deploymentId
	 *            流程ID
	 * @param diagramResourceName
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/showViewByTaskId")
	public void showViewByTaskId(String taskId,String diagramResourceName,
			HttpServletResponse response,String endtime) throws Exception {
		//根据taskId获得processInstanceId
		
		String processInstanceId = leaveService.findPngById(taskId);
		// 获取历史流程实例
		HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		// 获取流程图
		BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
		processEngineConfiguration = processEngine.getProcessEngineConfiguration();
		Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl) processEngineConfiguration);

		ProcessDiagramGenerator diagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
		ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processInstance.getProcessDefinitionId());

		List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery()
				.processInstanceId(processInstanceId).list();
		// 高亮环节id集合
		List<String> highLightedActivitis = new ArrayList<String>();
		// 高亮线路id集合
		List<String> highLightedFlows = getHighLightedFlows(definitionEntity, highLightedActivitList);

		/*
		 * for(HistoricActivityInstance tempActivity : highLightedActivitList){
		 * String activityId = tempActivity.getActivityId();
		 * highLightedActivitis.add(activityId); }
		 */
		// 当前流程实例执行到哪个节点
		ExecutionEntity execution = (ExecutionEntity) runtimeService.createExecutionQuery()
				.executionId(processInstanceId).singleResult();// 执行实例
		//highLightedActivitis.add(execution.getActivityId());

		// 中文显示的是口口口，设置字体就好了
		InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,
				highLightedFlows, "宋体", "宋体", null, 1.0);
		// 单独返回流程图，不高亮显示
		// InputStream imageStream =
		// diagramGenerator.generatePngDiagram(bpmnModel);
		// 输出资源内容到相应对象
		byte[] b = new byte[1024];
		int len;
		while ((len = imageStream.read(b, 0, 1024)) != -1) {
			response.getOutputStream().write(b, 0, len);
		}
	}

	/**
	 * 获取需要高亮的线
	 * 
	 * @param processDefinitionEntity
	 * @param historicActivityInstances
	 * @return
	 */
	private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinitionEntity,
			List<HistoricActivityInstance> historicActivityInstances) {
		List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
		for (int i = 0; i < historicActivityInstances.size() - 1; i++) {// 对历史流程节点进行遍历
			ActivityImpl activityImpl = processDefinitionEntity
					.findActivity(historicActivityInstances.get(i).getActivityId());// 得到节点定义的详细信息
			List<ActivityImpl> sameStartTimeNodes = new ArrayList<ActivityImpl>();// 用以保存后需开始时间相同的节点
			ActivityImpl sameActivityImpl1 = processDefinitionEntity
					.findActivity(historicActivityInstances.get(i + 1).getActivityId());
			// 将后面第一个节点放在时间相同节点的集合里
			sameStartTimeNodes.add(sameActivityImpl1);
			for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
				HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);// 后续第一个节点
				HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);// 后续第二个节点
				if (activityImpl1.getStartTime().equals(activityImpl2.getStartTime())) {
					// 如果第一个节点和第二个节点开始时间相同保存
					ActivityImpl sameActivityImpl2 = processDefinitionEntity
							.findActivity(activityImpl2.getActivityId());
					sameStartTimeNodes.add(sameActivityImpl2);
				} else {
					// 有不相同跳出循环
					break;
				}
			}
			List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();// 取出节点的所有出去的线
			for (PvmTransition pvmTransition : pvmTransitions) {
				// 对所有的线进行遍历
				ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition.getDestination();
				// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
				if (sameStartTimeNodes.contains(pvmActivityImpl)) {
					highFlows.add(pvmTransition.getId());
				}
			}
		}
		return highFlows;
	}

	/**
	 * 根据任务ID获得任务实例
	 * 
	 * @param taskId
	 *            任务ID
	 * @return
	 * @throws Exception
	 */
	private TaskEntity findTaskById(String taskId) throws Exception {
		TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(taskId).singleResult();
		if (task == null) {
			throw new Exception("任务实例未找到!");
		}
		return task;
	}
}
