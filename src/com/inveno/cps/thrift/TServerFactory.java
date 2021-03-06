package com.inveno.cps.thrift;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

import com.inveno.cps.common.util.Constants;
import com.inveno.cps.common.util.PropertiesUtil;

public class TServerFactory {

	private static String path = Constants.THRIFT_SEVER;

	private static int port = 9090;
	private static boolean ssl = false;
	private static String transport_type = "buffered";
	private static String protocol_type = "binary";
	private static String server_type = "thread-pool";

	public static TServer factory(ProcessorFactory processorFactory) {

		loadProperties();

		try {
			validate();
		} catch (Exception e) {
			e.printStackTrace();
		}

		TProtocolFactory tProtocolFactory = getTProtocolFactory();

		TTransportFactory tTransportFactory = getTTransportFactory();

		TServer serverEngine = null;

		TMultiplexedProcessor tProcessor = processorFactory.getProcessor();

		if (server_type.equals("nonblocking") || server_type.equals("threaded-selector")) {
			// Nonblocking servers
			TNonblockingServerSocket tNonblockingServerSocket = null;
			try {
				tNonblockingServerSocket = new TNonblockingServerSocket(
						new TNonblockingServerSocket.NonblockingAbstractServerSocketArgs().port(port));
			} catch (TTransportException e) {
				e.printStackTrace();
			}

			if (server_type.equals("nonblocking")) {
				// Nonblocking Server
				TNonblockingServer.Args tNonblockingServerArgs = new TNonblockingServer.Args(tNonblockingServerSocket);
				tNonblockingServerArgs.processor(tProcessor);
				tNonblockingServerArgs.protocolFactory(tProtocolFactory);
				tNonblockingServerArgs.transportFactory(tTransportFactory);

				serverEngine = new TNonblockingServer(tNonblockingServerArgs);
			} else { // server_type.equals("threaded-selector")
				// ThreadedSelector Server
				TThreadedSelectorServer.Args tThreadedSelectorServerArgs = new TThreadedSelectorServer.Args(
						tNonblockingServerSocket);
				tThreadedSelectorServerArgs.processor(tProcessor);
				tThreadedSelectorServerArgs.protocolFactory(tProtocolFactory);
				tThreadedSelectorServerArgs.transportFactory(tTransportFactory);

				serverEngine = new TThreadedSelectorServer(tThreadedSelectorServerArgs);

			}
		} else {
			// Blocking servers

			// SSL socket
			TServerSocket tServerSocket = null;
			if (ssl) {
				try {
					tServerSocket = TSSLTransportFactory.getServerSocket(port, 0);
				} catch (TTransportException e) {
					e.printStackTrace();
				}
			} else {
				try {
					tServerSocket = new TServerSocket(new TServerSocket.ServerSocketTransportArgs().port(port));
				} catch (TTransportException e) {
					e.printStackTrace();
				}
			}

			if (server_type.equals("simple")) {
				// Simple Server
				TServer.Args tServerArgs = new TServer.Args(tServerSocket);
				tServerArgs.processor(tProcessor);
				tServerArgs.protocolFactory(tProtocolFactory);
				tServerArgs.transportFactory(tTransportFactory);

				serverEngine = new TSimpleServer(tServerArgs);
			} else { // server_type.equals("threadpool")
				// ThreadPool Server
				TThreadPoolServer.Args tThreadPoolServerArgs = new TThreadPoolServer.Args(tServerSocket);
				tThreadPoolServerArgs.processor(tProcessor);
				tThreadPoolServerArgs.protocolFactory(tProtocolFactory);
				tThreadPoolServerArgs.transportFactory(tTransportFactory);

				serverEngine = new TThreadPoolServer(tThreadPoolServerArgs);
			}
		}
		// Set server event handler
		serverEngine.setServerEventHandler(new TServerEventHandlerImpl());

		return serverEngine;
	}

	private static void loadProperties() {
		port = Integer.parseInt(PropertiesUtil.getProperty(path, "port"));
		ssl = Boolean.parseBoolean(PropertiesUtil.getProperty(path, "ssl"));
		transport_type = PropertiesUtil.getProperty(path, "transport_type");
		protocol_type = PropertiesUtil.getProperty(path, "protocol_type");
		server_type = PropertiesUtil.getProperty(path, "server_type");
	}

	/**
	 * server type��simple,thread-pool,nonblocking,threaded-selector 
	 * protocol type:binary,json,compact 
	 * transport type:buffered,framed,fastframed
	 * 
	 * @throws Exception
	 */
	private static void validate() throws Exception {
		if (server_type.equals("simple")) {

		} else if (server_type.equals("thread-pool")) {

		} else if (server_type.equals("nonblocking")) {
			if (ssl == true) {
				throw new Exception("SSL is not supported over nonblocking servers!");
			}
		} else if (server_type.equals("threaded-selector")) {
			if (ssl == true) {
				throw new Exception("SSL is not supported over nonblocking servers!");
			}
		} else {
			throw new Exception("Unknown server type! " + server_type);
		}

		if (protocol_type.equals("binary")) {

		} else if (protocol_type.equals("json")) {

		} else if (protocol_type.equals("compact")) {

		} else {
			throw new Exception("Unknown protocol type! " + protocol_type);
		}

		if (transport_type.equals("buffered")) {

		} else if (transport_type.equals("framed")) {

		} else if (transport_type.equals("fastframed")) {

		} else {
			throw new Exception("Unknown transport type! " + transport_type);
		}
	}

	private static TProtocolFactory getTProtocolFactory() {
		TProtocolFactory tProtocolFactory = null;
		if (protocol_type.equals("json")) {
			tProtocolFactory = new TJSONProtocol.Factory();
		} else if (protocol_type.equals("compact")) {
			tProtocolFactory = new TCompactProtocol.Factory();
		} else {
			tProtocolFactory = new TBinaryProtocol.Factory();
		}
		return tProtocolFactory;
	}

	private static TTransportFactory getTTransportFactory() {
		TTransportFactory tTransportFactory = null;

		if (transport_type.equals("framed")) {
			tTransportFactory = new TFramedTransport.Factory();
		} else if (transport_type.equals("fastframed")) {
			tTransportFactory = new TFastFramedTransport.Factory();
		} else { // .equals("buffered") => default value
			tTransportFactory = new TTransportFactory();
		}
		return tTransportFactory;
	}
}
