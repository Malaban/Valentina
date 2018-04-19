package com.java.activiti.timer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTest {
	
	public static void main(String[] args) {
		Timer timer = new Timer();
		Task task = new Task();
		timer.schedule(task, new Date(), 1000);
	}
}
class Task extends TimerTask{
	public void run(){
		System.out.println("Timer¿ªÊ¼Ö´ÐÐ£¡");
	}
}