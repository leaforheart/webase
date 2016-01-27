package com.inveno.cps.common.baseclass;

import com.inveno.base.Pagin;
import com.inveno.util.StringUtil;


/**
 * formBean基类
 * @author yaoyuan
 */
public class BaseFormBean {
	
	private Pagin pagin;
	
	/** 到详细页时，是否需要刷新列表页，（直接进不用，更新完后进入需要） */
	private Boolean firstLoad = false;
	
	
	
	/**消息*/
	private String messages="";
	
	private String invenoToken;

	public String getMessages() {
		if(StringUtil.isEmpty(messages)){
			messages = "您的操作已成功";
		}
		return messages;
	}

	public void setMessages(String message) {
		this.messages = message;
	}

	public final Pagin getPagin() {
		return getPagin(true);
	}
	
	public final Pagin getPagin(boolean autoCreate){
		if(pagin==null && autoCreate){
			pagin = new Pagin(); 
		}
		return pagin;
	}

	public final void setPagin(Pagin pagin) {
		this.pagin = pagin;
	}

	public Boolean getFirstLoad() {
		return firstLoad;
	}

	public void setFirstLoad(Boolean firstLoad) {
		this.firstLoad = firstLoad;
	}

	public String getInvenoToken() {
		return invenoToken;
	}

	public void setInvenoToken(String invenoToken) {
		this.invenoToken = invenoToken;
	}

}
