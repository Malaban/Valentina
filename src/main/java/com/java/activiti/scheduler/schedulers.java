package com.java.activiti.scheduler;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class schedulers {
	private static final Logger logger = Logger.getLogger(schedulers.class);
	 public void excute() {
		 	logger.debug("��ʼ��Ŷ��");
			System.out.println("��ʱ������ִ��");
		}
}
