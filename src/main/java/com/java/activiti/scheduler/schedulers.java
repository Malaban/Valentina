package com.java.activiti.scheduler;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class schedulers {
	private static final Logger logger = Logger.getLogger(schedulers.class);
	 public void excute() {
		 	logger.debug("开始了哦！");
			System.out.println("定时器正在执行");
		}
}
