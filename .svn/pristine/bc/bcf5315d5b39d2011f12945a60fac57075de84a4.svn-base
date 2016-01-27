package com.inveno.cps.thrift;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;
import com.inveno.cps.common.util.Constants;
import com.inveno.cps.common.util.PropertiesUtil;
import com.inveno.util.StringUtil;

public class ClientPool {
	private static Logger log = Logger.getLogger(ClientPool.class);
	private static String path = Constants.THRIFT_CLIENT;
	
	private static Map<String,LinkedBlockingQueue<TServiceClient>> clientMap = null;
	private static ClientPool client = new ClientPool();
	
	public static synchronized TServiceClient getClient(String serviceName) {
		String[] ss = client.getServices();
		if(ss==null||ss.length==0) {
			log.error("client.properties中client属性没有配置对应的服务:"+serviceName);
			return null;//没有配置对应的服务
		}
		boolean flag = false;
		for(String s:ss) {
			String st = s.substring(s.lastIndexOf('.')+1);
			if(st.equals(serviceName)) {
				flag = true;
				break;
			}
		}
		if(!flag) {
			log.error("client.properties中client属性没有配置对应的服务:"+serviceName);
			return null;//没有配置对应的服务
		}
		//如果客户端map为空，就重新初始化一遍
		if(clientMap==null||clientMap.size()==0) {
			client.init();
			//初始化之后，如果还为空，说明初始化失败
			if(clientMap.size()==0) {
				log.error("cps客户端初始化失败");
				return null;
			}
		}
		
		LinkedBlockingQueue<TServiceClient> queue = clientMap.get(serviceName);
		if(queue==null) {
			client.initOneClient(serviceName);
			queue = clientMap.get(serviceName);
			if(queue==null) {
				log.error(serviceName+"客户端初始化失败，请检查服务名称和client.properties配置是否正确");
				return null;
			}
		}
		TServiceClient t = queue.poll();
		//如果队列中的客户端用光了，就新建一个
		if(t==null) {
			t = client.newClient(client.getFullName(serviceName));
		}
		if(t!=null) {
			log.info("成功获取"+serviceName+"客户端");
		}
		log.info("==============================="+clientMap);
		return t;
	}
	
	public static void recoverClient(String serviceName,TServiceClient t) {
		LinkedBlockingQueue<TServiceClient> queue = clientMap.get(serviceName);
		if(queue==null) {
			log.info(serviceName+"客户端回收失败，请检查cps连接，以及提供的服务名称是否有误");
			return;
		}
		queue.add(t);
	}
	
	private void init() {
		clientMap = new HashMap<String,LinkedBlockingQueue<TServiceClient>>();
		int cpu = Runtime.getRuntime().availableProcessors();
		String[] services = getServices();
		for(String service:services) {
			LinkedBlockingQueue<TServiceClient> queue = new LinkedBlockingQueue<TServiceClient>();
			String serviceName = service.substring(service.lastIndexOf('.')+1);
			//先new一个客户端，看下能不能new成功
			TServiceClient client = newClient(service);
			if(client!=null) {
				queue.add(client);
				for(int i=0;i<cpu*2-1;i++) {
					queue.add(newClient(service));
				}
				clientMap.put(serviceName, queue);
			}
		}
	}
	
	private void initOneClient(String serviceName) {
		LinkedBlockingQueue<TServiceClient> queue = new LinkedBlockingQueue<TServiceClient>();
		int cpu = Runtime.getRuntime().availableProcessors();
		String service = getFullName(serviceName);
		if(StringUtil.isEmpty(service)) {
			return;
		}
		TServiceClient client = newClient(service);
		if(client!=null) {
			queue.add(client);
			for(int i=0;i<cpu*2-1;i++) {
				queue.add(newClient(service));
			}
			clientMap.put(serviceName, queue);
		}
	}
	
	private String getFullName(String serviceName) {
		String[] services = client.getServices();
		String service = null;
		for(String serviceTmp:services) {
			if(serviceName.equals(serviceTmp.substring(serviceTmp.lastIndexOf('.')+1))) {
				service = serviceTmp;
				break;
			}
		}
		return service;
	}
	
	@SuppressWarnings("unchecked")
	private <T extends TServiceClient> T newClient(String service) {
		T t = null;
		try {
			Constructor<T> c = (Constructor<T>) Class.forName(service+"$Client").getConstructor(TProtocol.class);
			String serviceName = service.substring(service.lastIndexOf('.')+1);
			t = c.newInstance(ClientProtocolFactory.factory(serviceName));
			if(!t.getInputProtocol().getTransport().isOpen()) {
				t.getInputProtocol().getTransport().open();
			}
		} catch (Throwable e) {
			log.error(service+"客户端创建失败");
			log.error(e.toString());
			return null;
		}
		return t;
	}
	
	private String[] getServices() {
		String services = PropertiesUtil.getProperty(path, "client");
		String[] service = services.split(",");
		return service;
	}
}
