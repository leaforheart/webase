package com.inveno.cps.thrift;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.ServerContext;
import org.apache.thrift.server.TServerEventHandler;
import org.apache.thrift.transport.TTransport;

public class TServerEventHandlerImpl implements TServerEventHandler {

	@Override
	public void preServe() {
		
	}

	@Override
	public ServerContext createContext(TProtocol paramTProtocol1, TProtocol paramTProtocol2) {
		return null;
	}

	@Override
	public void deleteContext(ServerContext paramServerContext, TProtocol paramTProtocol1, TProtocol paramTProtocol2) {
		
	}

	@Override
	public void processContext(ServerContext paramServerContext, TTransport paramTTransport1,TTransport paramTTransport2) {
		
	}

}
