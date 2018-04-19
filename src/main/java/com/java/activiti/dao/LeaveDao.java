package com.java.activiti.dao;

import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;

import com.java.activiti.model.Leave;

/**
 * 业务管理
 * @author Administrator
 *
 */
public interface LeaveDao {
		public List<Leave> leavePage(Map<String,Object> map);
		
		public int leaveCount(Map<String,Object> map);
		
		public int addLeave(Leave leave);
		
		public Leave findById(String id);
		
		public int updateLeave(Leave leave);
		
		public int updateLeaves(Leave leave);
		
		public Leave getLeaveByTaskId(String processInstanceId);
		
		public int deleteLeave(Leave leave);

		public String findByExecutionId(String processInstanceId);

		public int deleteprocessInstanceId(String processInstanceId);

		public int deleteprocessInstanceIds(String processInstanceId);

		public String findByProcessInstanceId(String processInstanceId);

		public int deleteprocessInstanceIdes(String processInstanceId);

		public int deleteprocessInstanceIdess(String processInstanceId);

		public int deleteprocessInstanceIdsss(String processInstanceId);

		public String findPngById(String taskId);

		public String findTaskByTaskId(String taskId);
}
