package com.inveno.cps.thrift;

import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import com.inveno.cps.authenticate.thrift.AuthenticateService;
import com.inveno.cps.thrift.ClientProtocolFactory;

public class TestClient {
	
	public static void main(String[] args) {
		AuthenticateService.Client authenClient = new AuthenticateService.Client(ClientProtocolFactory.factory("AuthenticateService"));
		if(!authenClient.getInputProtocol().getTransport().isOpen()) {
			try {
				authenClient.getInputProtocol().getTransport().open();
			} catch (TTransportException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Map<String,String> map = authenClient.login("yilin.xiao@inveno.cn", "111111");
			System.out.println(map);
		} catch (TException e) {
			e.printStackTrace();
		}finally {
			authenClient.getInputProtocol().getTransport().close();
		}
	}
	
}
