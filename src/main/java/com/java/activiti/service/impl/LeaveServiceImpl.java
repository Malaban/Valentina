package com.java.activiti.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.java.activiti.dao.LeaveDao;
import com.java.activiti.model.Leave;
import com.java.activiti.service.LeaveService;
@Service("leaveService")
public class LeaveServiceImpl implements LeaveService{
	@Resource 
	private LeaveDao leaveDao;
	public List<Leave> leavePage(Map<String,Object> map){
		return leaveDao.leavePage(map);
	}
	
	public int leaveCount(Map<String,Object> map){
		return leaveDao.leaveCount(map);
	}
	
	public int addLeave(Leave leave){
		return leaveDao.addLeave(leave);
	}
	
	public Leave findById(String id){
		return leaveDao.findById(id);
	}
	
	public int updateLeave(Leave leave){
		return leaveDao.updateLeave(leave);
	}
	
	public Leave getLeaveByTaskId(String processInstanceId){
		return leaveDao.getLeaveByTaskId(processInstanceId);
	}

	public int deleteLeave(Leave leave) {
		return leaveDao.deleteLeave(leave);
	}

	public int updateLeaves(Leave leave) {
		return leaveDao.updateLeaves(leave);
	}

	public String findByExecutionId(String processInstanceId) {
		return leaveDao.findByExecutionId(processInstanceId);
	}

	public int deleteprocessInstanceId(String processInstanceId) {
		return leaveDao.deleteprocessInstanceId(processInstanceId);
	}

	public int deleteprocessInstanceIds(String processInstanceId) {
		return leaveDao.deleteprocessInstanceIds(processInstanceId);
	}

	public String findByProcessInstanceId(String processInstanceId) {
		return leaveDao.findByProcessInstanceId(processInstanceId);
	}

	public int deleteprocessInstanceIdes(String processInstanceId) {
		return leaveDao.deleteprocessInstanceIdes(processInstanceId);
	}

	public int deleteprocessInstanceIdess(String processInstanceId) {
		return leaveDao.deleteprocessInstanceIdess(processInstanceId);
	}

	public int deleteprocessInstanceIdsss(String processInstanceId) {
		return leaveDao.deleteprocessInstanceIdsss(processInstanceId);
	}

	public String findPngById(String taskId) {
		return leaveDao.findPngById(taskId);
	}

	public String findTaskByTaskId(String taskId) {
		return leaveDao.findTaskByTaskId(taskId);
	}
}
