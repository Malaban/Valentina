package com.java.activiti.webservice;

import java.util.Date;

import javax.jws.WebService;
@WebService(endpointInterface="com.java.activiti.webservice.HelloWorld" ,serviceName="HelloWorldWs")
public class HelloWorlds implements HelloWorld {

	public String SayHi(String name) {
		return name +"您好，现在时间是："+new Date();
	}

}
