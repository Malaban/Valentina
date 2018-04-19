package com.java.activiti.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.ProcessDefinitionImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.java.activiti.model.Leave;
import com.java.activiti.model.MemberShip;
import com.java.activiti.model.PageInfo;
import com.java.activiti.model.User;
import com.java.activiti.service.LeaveService;
import com.java.activiti.util.DateJsonValueProcessor;
import com.java.activiti.util.ResponseUtil;
import com.java.activiti.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * 业务处理
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/leave")
public class LeaveController {

	@Resource
	private LeaveService leaveService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;
	@Resource
	private RepositoryService repositoryService;

	private ProcessInstance pi;

	/**
	 * 分页查询业务
	 * 
	 * @param response
	 * @param rows
	 * @param page
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/leavePage")
	public String leavePage(HttpServletResponse response, String rows, String page, String userId) throws Exception {
		PageInfo<Leave> leavePage = new PageInfo<Leave>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		Integer pageSize = Integer.parseInt(rows);
		leavePage.setPageSize(pageSize);
		if (page == null || page.equals("")) {
			page = "1";
		}
		leavePage.setPageIndex((Integer.parseInt(page) - 1) * pageSize);
		map.put("pageIndex", leavePage.getPageIndex());
		map.put("pageSize", leavePage.getPageSize());
		int leaveCount = leaveService.leaveCount(map);
		List<Leave> leaveList = leaveService.leavePage(map);
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd hh:mm:ss"));
		JSONObject result = new JSONObject();
		JSONArray jsonArray = JSONArray.fromObject(leaveList, jsonConfig);
		result.put("rows", jsonArray);
		result.put("total", leaveCount);
		ResponseUtil.write(response, result);
		return null;
	}

	/**
	 * 添加请假单
	 * 
	 * @param leave
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/save")
	public String save(Leave leave, HttpServletResponse response, String userId) throws Exception {
		User user = new User();
		user.setId(userId);
		int resultTotal = 0;
		leave.setLeaveDate(new Date());
		leave.setFlag("1");
		// 添加用户对象
		leave.setUser(user);
		String ProcessInstanceId="";
		leave.setProcessInstanceId(ProcessInstanceId);
		resultTotal = leaveService.addLeave(leave);
		JSONObject result = new JSONObject();
		if (resultTotal > 0) {
			result.put("success", true);
		} else {
			result.put("success", false);
		}
		ResponseUtil.write(response, result);
		return null;
	}

	/**
	 * 提交請假流程申請
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/startApply")
	public String startApply(HttpServletResponse response, String leaveId) throws Exception {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("leaveId", leaveId);
		// 启动流程
		pi = runtimeService.startProcessInstanceByKey("activitiemployeeProcess", variables);
		// 根据流程实例Id查询任务
		Task task = taskService.createTaskQuery().processInstanceId(pi.getProcessInstanceId()).singleResult();
		// 完成 学生填写请假单任务
		taskService.complete(task.getId());
		Leave leave = leaveService.findById(leaveId);
		// 修改状态
		leave.setState("审核中");
		leave.setProcessInstanceId(pi.getProcessInstanceId());
		// 修改请假单状态
		leaveService.updateLeave(leave);
		JSONObject result = new JSONObject();
		result.put("success", true);
		ResponseUtil.write(response, result);
		return null;
	}

	/**
	 * 查询流程信息
	 * 
	 * @param response
	 * @param taskId
	 *            流程实例ID
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getLeaveByTaskId")
	public String getLeaveByTaskId(HttpServletResponse response, String leaveId, String taskId) throws Exception {
		// 先根据流程ID查询
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		Leave leave = leaveService.getLeaveByTaskId(task.getProcessInstanceId());
		JSONObject result = new JSONObject();
		result.put("leave", JSONObject.fromObject(leave));
		ResponseUtil.write(response, result);
		return null;
	}

	/**
	 * 挂起该流程实例
	 * 
	 * @param response
	 * @param taskId
	 *            流程实例ID
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/backApply")
	public String backApply(HttpServletResponse response, HttpServletRequest request, String processInstanceId,
			String id) throws Exception {
		
		/***
		 * 1.首先根据processInstanceId查找act_ru_task表，得到taskID
		 * 2.根据得到的taskID删除act_ru_identitylink标的相关记录(因为act_ru_identitylink与act_ru_task有主外键关联)
		 * 3.根据processInstanceId删除act_ru_task标的相关记录
		 * 4.根据processInstanceId删除act_ru_variable标的相关记录
		 * 5.根据processInstanceId删除act_ru_identitylink标的相关记录(因为act_ru_identitylink与act_ru_execution有主外键关联)
		 * 6.根据processInstanceId删除act_ru_execution标的相关记录
		 */
		String taskID=leaveService.findByProcessInstanceId(processInstanceId);//act_ru_task
		leaveService.deleteprocessInstanceIds(taskID);	//act_ru_identitylink
		leaveService.deleteprocessInstanceId(processInstanceId);//act_ru_task
		leaveService.deleteprocessInstanceIdess(processInstanceId);//act_ru_variable
		leaveService.deleteprocessInstanceIdsss(processInstanceId);	//act_ru_identitylink
		leaveService.deleteprocessInstanceIdes(processInstanceId);//act_ru_execution
		
		// 将t_leave表的相关字段改为撤销标志
		Leave leaves = new Leave();
		leaves.setFlag("0");
		leaves.setId(Integer.parseInt(id));
		int flag = leaveService.updateLeaves(leaves);
		JSONObject result = new JSONObject();
		if (flag > 0) {
			result.put("success", true);
		} else {
			result.put("success", false);
		}
		ResponseUtil.write(response, result);
		return null;
	}

	/**
	 * 
	 * @param response
	 * @param leaveId
	 *            主键
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/deleteApply")
	public String deleteApply(HttpServletResponse response, String leaveId) throws Exception {
		Leave leave = new Leave();
		leave.setId(Integer.parseInt(leaveId));
		leave.setFlag("2");
		int results = leaveService.updateLeaves(leave);
		JSONObject result = new JSONObject();
		if (results > 0) {
			result.put("success", true);
		} else {
			result.put("success", false);
		}
		ResponseUtil.write(response, result);
		return null;
	}

	/**
	 * 将节点之后的节点删除然后指向新的节点。
	 * 
	 * @param actDefId
	 *            流程定义ID
	 * @param nodeId
	 *            流程节点ID
	 * @param aryDestination
	 *            需要跳转的节点
	 * @return Map 返回节点和需要恢复节点的集合。
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Map prepare(String actDefId, String nodeId, String[] aryDestination) {
		Map map = new HashMap();

		// 修改流程定义
		ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(actDefId);

		ActivityImpl curAct = processDefinition.findActivity(nodeId);
		List outTrans = curAct.getOutgoingTransitions();
		try {
			List cloneOutTrans = (List) clone(outTrans);
			map.put("outTrans", cloneOutTrans);
		} catch (Exception ex) {

		}
		curAct.getOutgoingTransitions().clear();
		if (aryDestination != null && aryDestination.length > 0) {
			for (String dest : aryDestination) {
				// 创建一个连接
				ActivityImpl destAct = processDefinition.findActivity(dest);
				TransitionImpl transitionImpl = curAct.createOutgoingTransition();
				transitionImpl.setDestination(destAct);
			}
		}
		map.put("activity", curAct);
		return map;
	}

	private Object clone(final Object value) throws IOException, ClassNotFoundException {
		// 字节数组输出流，暂存到内存中
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 序列化
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(value);
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);
		// 反序列化
		return ois.readObject();
	}

	/**
	 * 将临时节点清除掉，加回原来的节点。
	 * 
	 * @param map
	 *            void
	 */
	@SuppressWarnings("unchecked")
	private void restore(Map map) {
		ActivityImpl curAct = (ActivityImpl) map.get("activity");
		List outTrans = (List) map.get("outTrans");
		curAct.getOutgoingTransitions().clear();
		curAct.getOutgoingTransitions().addAll(outTrans);
		// for (Iterator it = outTrans.iterator(); it.hasNext();) {
		// // 回复删除的INCOMING
		// PvmTransition transition = it.next();
		// PvmActivity activity = transition.getDestination();
		// List incomes = (List) map.get("Income_" + activity.getId());
		// activity.getIncomingTransitions().clear();
		// activity.getIncomingTransitions().addAll(incomes);
		// }
	}
}
