package com.java.activiti.webservice;

import javax.xml.ws.Endpoint;

public class ServiceMain {
  public static void main(String[] args) {
	HelloWorld hw   = new HelloWorlds();
	//����Endpoint��publish��������Web Service  
	Endpoint.publish("http://127.0.0.1:8081/Activiti-LFP", hw);
	System.out.println("Web Service��¶�ɹ���");
}
}
