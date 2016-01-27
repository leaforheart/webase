package com.inveno.cps.thrift;

import org.apache.thrift.server.TServer;

public class BootStrap implements Runnable {
	private ProcessorFactoryImpl processorFactory;

	public ProcessorFactoryImpl getProcessorFactory() {
		return processorFactory;
	}

	public void setProcessorFactory(ProcessorFactoryImpl processorFactory) {
		this.processorFactory = processorFactory;
	}
	
	public void start() {
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		TServer server = TServerFactory.factory(processorFactory);
		System.out.println("cps thrift server is start.............");
		server.serve(); 
	}
}
