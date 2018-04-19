package com.java.activiti.webservice;

import javax.jws.WebService;

@WebService
public interface HelloWorld {
	String SayHi(String name);
}
