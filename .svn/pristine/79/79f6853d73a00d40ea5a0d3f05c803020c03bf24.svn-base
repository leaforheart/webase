package com.inveno.cps.common.baseclass;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.inveno.cps.common.exception.BusinessException;
import com.inveno.cps.common.util.Constants;
import com.inveno.util.MemCachedUtil;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 所有Action 基类 2011-1-12 author:yaoyuan
 * 
 */
public class BaseAction extends ActionSupport implements ServletResponseAware,
		ServletRequestAware {
	private static final long serialVersionUID = 1L;
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	/**easyUI框架提交的分页参数
	 * xjr 2014-05-23
	 */
	private int page;
	private int rows;
	private String q;
	
	/** struts2从2.1升级到2.3后会报这两个参数不存在的警告，但不影响使用。为了不显示恶心的警告信息，故加上这两个参数 */
	private String _;
	private String post;
	
	// 记录登录后回跳面
	private String returnUrl;
	
	//首页action
	public static String index="index.action";

	/**广告ad*/
	public static String ad;
	
	public String getIndex() {
		return index;
	}

	@SuppressWarnings("static-access")
	public void setIndex(String index) {
		this.index = index;
	}

	public String getReturnUrl() {
		try {
			return URLDecoder.decode(returnUrl, "UTF-8");
		} catch (Exception e) {
			return "";
		}
		
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	protected void saveMessage(String msg) {
		getRequest().setAttribute("msg", msg);
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public HttpSession getSession() {
		return getSession(true);
	}

	public HttpSession getSession(boolean autoCreate) {
		HttpSession session = getRequest().getSession(autoCreate);
		if (!autoCreate && session == null) {
			throw new BusinessException("对不起，您未登陆或已超时");
		}
		return session;
	}

	protected void disposeSession() {
		// 在监听器中统一销毁
		try {
			getSession(false).invalidate();
		} catch (Exception e) {
			// 注销，不接受异常
		}
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	/**
	 * 运用j2ee api将数据发给客户端 2011-1-13
	 * 
	 * @param content
	 * @throws IOException
	 */
	protected void sendClient(String content) throws IOException {
		PrintWriter out = getResponseWriter();
		out.print(content);
		out.flush();
		out.close();
	}

	/**
	 * 运用j2ee api将数据发给客户端 2011-1-13
	 * 
	 * @param content
	 * @throws IOException
	 */
	protected void writeToClient(String content) {
		try {
			sendClient(content);
		} catch (IOException e) {
			// throw new ServiceException(e.getMessage(),e);
		}
	}

	private PrintWriter getResponseWriter() throws IOException {
		HttpServletResponse response = getResponse();
		response.setContentType("text/html;charset=utf-8");
		return response.getWriter();
	}

	/**
	 * 向相应中写入字符串信息 2011-1-13
	 * 
	 * @author yaoyuan
	 * @param msg
	 */
	protected void print(String msg) {
		try {
			getResponseWriter().print(msg);
		} catch (IOException e) {
		}
	}

	protected void closeWriter() {
		try {
			getResponseWriter().flush();
			getResponseWriter().close();
		} catch (IOException e) {
		}
	}

	/**
	 * 从缓存中得到合作厂商账户
	 * @author wangdesheng
	 * @date 2012-8-31
	 * @return
	 */
/*	public FirmAccount getFirmAccount(){
		return (FirmAccount) MemCachedUtil.getSessionAttribute(Constants.FIRM_LOGIN_KEY);
	}*/

	public String getAd() {
		return ad;
	}

	public void setAd(String ad) {
		BaseAction.ad = ad;
	}
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public String get_() {
		return _;
	}

	public void set_(String _) {
		this._ = _;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}
	
}
