package com.inveno.cps.thrift;

import org.apache.thrift.server.TServer;

public class TestServer {
	public static void main(String[] args) {
		ProcessorFactoryImpl processorFactory = new ProcessorFactoryImpl();
		TServer server = TServerFactory.factory(processorFactory);
		System.out.println("common server is start.............");
		server.serve();
	}
}
