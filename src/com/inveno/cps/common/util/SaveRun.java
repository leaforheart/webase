package com.inveno.cps.common.util;

import java.util.List;

import org.apache.log4j.Logger;

import com.inveno.util.CollectionUtils;
import com.inveno.util.StringUtil;


/**
 * 多线程保存信息类
 * 
 * @author zhiwen.wang
 * @date 2011-12-27
 * 
 */
public class SaveRun extends Thread {
	/** 要保存的信息类型 */
	private String saveType;
	/** 营销列表 **/
	private List<Object> marketList;
	
	
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(this.getClass());
	public SaveRun(String saveType) {
		this.saveType = saveType;
	}

	/**
	 * 多线程执行方�?
	 * 
	 * @author dazi.wei
	 * @date 2013-1-24
	 */
	@Override
	public void run() {
		if(getSaveType().equals(Constants.MARKET_OFFLINE)){
			updateMarketSetStateOffline();
		}
	}
	/**
	 * 修改广告状�?为下线状�?
	 * @Title: updateMarketSetStateOffline
	 * @author: liming
	 * @return void
	 * @throws
	 * @data 2014�?�?4�?
	 */
	private void updateMarketSetStateOffline() {
/*		StringBuilder sql = new StringBuilder(10);
		StringBuilder ids = new StringBuilder(10);
		//获取�?��更新的广�?
		if(CollectionUtils.isNotEmpty(marketList)){
			for (Object object : marketList) {
				Market market = (Market) object;
				if(market.getIsOutOnline().equals("1")&&market.getState()!=4){
					if(StringUtil.isNotEmpty(ids.toString())){
						ids.append(",");
					}
					ids.append(market.getId());
				}
			}
		}
		//更新状�?
		if(StringUtil.isNotEmpty(ids.toString())){
			sql.append("update t_market set state = 4 where id in(");
			sql.append(ids);
			sql.append(")");
			SysContext.anyDao.excuteSql(sql.toString(), null);
		}
		*/
	}

	public String getSaveType() {
		return saveType;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

	public List<Object> getMarketList() {
		return marketList;
	}

	public void setMarketList(List<Object> marketList) {
		this.marketList = marketList;
	}
	
}
