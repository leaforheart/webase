package com.inveno.cps.common.util;
/**
 * 数据字典的parent_id,child_id
 *
 * 2011-2-10
 * @author yaoyuan
 */
public interface Dictionary {
	
	int STATE_PUBLISH_OK = 15;
	
	/**查询不是删除数据时，小于等于98 */
	int STATE_NOT_DEL = 98;
	/**应用分类（liming�?/
	String APP_TYPE = "84";
	
	/**数据词典 过滤条件(liming)*/
	String DICTIONRY_DD_FILTER = "84,93,94,96,97,109,110,113,111";
	
	/**营销类型（dazi�?/
	String MARKET_TYPE = "96";
	int STATE_NOT_VERIFY = 1;
	
	/**各种营销类型 xjr 2013-01-22*/
	int MARKETTYPE_ONE = 1;    //链接�?
	int MARKETTYPE_TWO = 2;    //应用�?
	int MARKETTYPE_THREE = 3;  //互动�?
	int MARKETTYPE_FOUR = 4;  //点击通话�?
	int MARKETTYPE_FIVE = 5;  //品牌推广
	int MARKETTYPE_SIX= 6;  //视频�?
	int MARKETTYPE_SENVEN = 7;  //音频�?
	int MARKETTYPE_NOVEL = 8; //小说�?
	
	/**用户角色类型（liming�?/
	String PMUSERTYPE = "97";
	
	/**厂商（liming�?/
	String FIR ="1";
	/**运营商（liming�?/
	String OPE ="2";
	/**大众（liming�?/
	String PUB="3";
	/**代理商（liming�?/
	String AGE ="4";
	/**管理（liming�?/
	String MAN ="5";
	/**大众**/
	String DAZHONG="3";
	
	String  ALL_CHINA_CITY = "-1" ;
	
	
	/*****************************资讯的状�?wangdesheng 2013-05-08**/
	/**已发�?15*/
	int STATE_VERIFY_OK = 15;
	/**已审�?10*/
	String STATE_VERIFY_OK_VALUE = "已发";
	/**删除 99*/
	int STATE_DEL = 99;
	/**删除 99*/
	String STATE_DEL_VALUE = "删除";
	
	/*****************************RSS的状�?wangdesheng 2013-05-08**/
	/**已合�?*/
	int RSS_STATE_VERIFY = 3;
	/*****************************频道的状�?wangdesheng 2013-05-08**/
	/**已发�?15*/
	int CHANNEL_STATE_VERIFY = 15;
	
	
	/**（API）合作�?数据类型（liming�?/
	String DATA_TYPE = "110";
	
	/** 来源渠道项目名的数字词典parent_id 120 */
	String PRO_ORIGIN = "120";
	
	/** 设备类型信息parent_id  */
	//String DEVICES_TYPE = "113";
	String DEVICES_TYPE = "138";
	
	/** 产品类型信息parent_id */
	String PRODUCT_TYPE = "111";
	
	String BASEURL = "baseurl";
	
	String SERVER_URL = "serverUrl";	
	
	/** 数字词典广告业务类型parent_id  */
	String ADTYPE = "129";
	
	/** 数字词典广告业务审核状�?—�?待审�? */
	int ADTYPE_CHECKING = 1;
	
	/** 数字词典广告业务审核状�?—�?已审核待上线  */
	int ADTYPE_CHECKED = 2;
	
	/** 数字词典广告业务审核状�?—�?已上�? */
	int ADTYPE_ONLINE = 3;
	
	/** 数字词典广告业务审核状�?—�?已下�? */
	int ADTYPE_UPDERLINE = 4;
	
	/** 数字词典广告业务审核状�?—�?审核不�?�? */
	int ADTYPE_NOPASS = 5;
	
	/**
	 * �?��模式parent_id,WDS
	 */
	String SELL_TYPE = "137";
	
	/** 设备价位信息parent_id  */
	String DEVICES_PRICE_TYPE = "139";
	
	/**用户启用**/
	String USER_STATE_ENABLE="2";

	String USER_TYPE = "128";
	
	
}
