package com.inveno.cps.common.util;

/**
 * 
 * 系统常量定义接口
 * 此类中变量默认为 public final static 类型
 * 2011-1-12
 * @author yaoyuan
 *
 */
public interface Constants{
	
	String SESSION_IDENTIFIER = "sessionid";//会话标识
	String MARKET_OFFLINE = "updateMarketSetStateOffline";
	String AUTHCODE_KEY = "inveno";
	String RETURN_CODE = "rCode";//返回码
	String RETURN_DATA = "data";
	String SERVER_CODE = "-666";//cps程序异常时的返回码值
	String SUCCESS_CODE = "0";//成功返回时，返回的返回码值
	String ROOT_PARENT="-9";//根目录的父id
	
	//permission类型
	String CATALOG_TYPE= "0";
	String PAGE_TYPE="1";
	String BUTTON_TYPE="2";
	
	String REDIS_PATH = "com/inveno/cps/redis/redis.properties";
	String THRIFT_SEVER ="com/inveno/cps/thrift/server.properties";
	String THRIFT_CLIENT="com/inveno/cps/thrift/client.properties";
	String PERMISSION_TEMPLATE="com/inveno/cps/authority/model/permission.xml";
	String PERMISSION_TEMPLATE_NAME="permission_template.xml";
	String NEWEST_PERMISSION="_newest_permission.xml";
	
	String CONNECT_ERROR = "-999";
	String CLIENT_ERROR = "-888";
	
	String PROJECTS = "projects";
	String CUR_PROJECT = "curProject";
	
	String ROLE_CHECKED="1";
	String ROLE_UNCHECKED="0";
	
	String ROLEOFGOD = "超级管理员";
	String USEROFGOD = "admin";
	
	int PASS = 0;
	int NO_STARTSTATE = -101;
	int MUTI_STARTSTATE = -102;
	int NO_GOODENDSTATE = -103;
	int MUTI_GOODENDSTATE = -104;
	int NO_BADENDSTATE = -105;
	int MUTI_BADENDSTATE = -106;
	int NO_NEXT = -107;
	int STATE_TYPE_ERROR = -108;
	int STATE_NAME_EMPTY = -109;
	int STATE_NAME_TOOLONG = -110;
	int ACTION_NO_ACTOR = -111;
	int ACTION_ACTOR_TYPE_ERROR = -112;
	int ACTION_NAME_EMPTY = -113;
	int ACTION_NAME_TOOLONG = -114;
	
	String ROLE_CHECK="1";
	String USER_CHECK="2";
	
	String INIT_ID="0";
}
