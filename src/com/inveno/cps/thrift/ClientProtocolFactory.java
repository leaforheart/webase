package com.inveno.cps.thrift;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFastFramedTransport;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.inveno.cps.common.util.Constants;
import com.inveno.cps.common.util.PropertiesUtil;

public class ClientProtocolFactory {
	
	private static String path = Constants.THRIFT_CLIENT;
	
	private static String host = "localhost";
	private static int port = 9090;
	private static String protocol_type = "binary";
	private static String transport_type = "buffered";
	private static boolean ssl = false;
	private static int socketTimeout = 1000;
	
	public static TMultiplexedProtocol factory(String serviceName) {
		loadProperties();
		
		try {
			validate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TTransport transport = getTTransport();
		
		TProtocol tProtocol = getTProtocol(transport);
		
		TMultiplexedProtocol tmProtocol = new TMultiplexedProtocol(tProtocol,serviceName);
		
		return tmProtocol;
	}
	
	public static TProtocol factory() {
		loadProperties();
		
		try {
			validate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TTransport transport = getTTransport();
		
		TProtocol tProtocol = getTProtocol(transport);
		return tProtocol;
	}
	
	private static void loadProperties() {
		host = PropertiesUtil.getProperty(path, "host");
		port = Integer.parseInt(PropertiesUtil.getProperty(path, "port"));
		protocol_type = PropertiesUtil.getProperty(path, "protocol_type");
		transport_type = PropertiesUtil.getProperty(path, "transport_type");
		ssl = Boolean.parseBoolean(PropertiesUtil.getProperty(path, "ssl"));
		socketTimeout = Integer.parseInt(PropertiesUtil.getProperty(path, "socketTimeout"));
	}
	
	private static void validate() throws Exception {
		if (protocol_type.equals("binary")) {
			
	      } else if (protocol_type.equals("compact")) {
	    	  
	      } else if (protocol_type.equals("json")) {
	    	  
	      } else {
	        throw new Exception("Unknown protocol type! " + protocol_type); 
	      }
	      if (transport_type.equals("buffered")) {
	    	  
	      } else if (transport_type.equals("framed")) {
	    	  
	      } else if (transport_type.equals("fastframed")) {
	    	  
	      } else if (transport_type.equals("http")) {
	    	  
	      } else {
	        throw new Exception("Unknown transport type! " + transport_type);
	      }
	      if (transport_type.equals("http") && ssl == true) {
	        throw new Exception("SSL is not supported over http.");
	      }
	}
	
	@SuppressWarnings("resource")
	private static TTransport getTTransport() {
		TTransport transport = null;

	    try {
	      if ("http".equals(transport_type)) {
	        String url = "http://" + host + ":" + port + "/service";
	        transport = new THttpClient(url);
	      } else {
	        TSocket socket = null;
	        if (ssl == true) {
	          socket = TSSLTransportFactory.getClientSocket(host, port, 0);
	        } else {
	          socket = new TSocket(host, port);
	        }
	        socket.setTimeout(socketTimeout);
	        transport = socket;
	        if (transport_type.equals("buffered")) {
	        } else if (transport_type.equals("framed")) {
	          transport = new TFramedTransport(transport);
	        } else if (transport_type.equals("fastframed")) {
	          transport = new TFastFramedTransport(transport);
	        }
	      }
	    } catch (Exception x) {
	      x.printStackTrace();
	      throw new RuntimeException("创建transport失败"+x.getMessage());
	      //System.exit(1);
	    }
	    
	    return transport;
	}
	
	private static TProtocol getTProtocol(TTransport transport) {
		TProtocol tProtocol = null;
	    if (protocol_type.equals("json")) {
	      tProtocol = new TJSONProtocol(transport);
	    } else if (protocol_type.equals("compact")) {
	      tProtocol = new TCompactProtocol(transport);
	    } else {
	      tProtocol = new TBinaryProtocol(transport);
	    }
	    return tProtocol;
	}
	
	
}
