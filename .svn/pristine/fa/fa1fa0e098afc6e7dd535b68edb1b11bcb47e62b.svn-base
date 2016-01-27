package com.inveno.cps.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.inveno.util.PropertyUtils;


/**
 * 创建mta_DB数据库的连接
 * 主要用于根据单点登录上来的帐号�?密码和模块加载菜�?
 * @author xuanjunren
 * @date 2012-12-11
 */
public class MtaDataBaseConn {
	
	// 数据库的连接手柄
	private Connection conn = null;

	// 执行SQL语句手柄
	private PreparedStatement ps = null;

	private Statement stmt = null;

	public MtaDataBaseConn() {
		getDbConn();
	}
	
	/**
	 * 获得数据库连�?
	 * @author xuanjunren
	 * @date 2012-11-7
	 */
	public void getDbConn() {
		String driverName = PropertyUtils.getProperty("jdbc.driverClassName","/db-connection.properties");
		String dataUrl = PropertyUtils.getProperty("jdbc.url","/db-connection.properties");
		String dataUser = PropertyUtils.getProperty("jdbc.username","/db-connection.properties");
		String dataPass = PropertyUtils.getProperty("jdbc.password","/db-connection.properties");
		try {
			// 实例化MySql驱动
			Class.forName(driverName);
			// 连接数据库并返回数据库的连接手柄
			conn = DriverManager.getConnection(dataUrl, dataUser, dataPass);
			stmt = conn.createStatement();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获得数据库连�?
	 * @author xuanjunren
	 * @date 2012-11-7
	 */
	public void closeConn() {
		// 关闭执行SQL语句的手�?
		if (ps != null) {
			try {
				ps.close();
				ps = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 关闭数据库连�?
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 执行插入和更新操�?
	 * @author wangdesheng
	 * @date 2012-12-11
	 * @param strSql
	 * @return
	 */
	public int executeSql(String strSql) {
		int num = 0;
		try {
			// 创建�?��执行SQL语句的手�?
			ps = conn.prepareStatement(strSql);
			num = ps.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return num;
	}
	
	/**
	 * 执行查询语句
	 * @author xuanjunren
	 * @date 2012-12-11
	 * @param strSql
	 * @return
	 */
	public ResultSet findBySql(String strSql) {
		ResultSet rs = null;
		try {
			// 创建�?��执行SQL语句的手�?
			ps = conn.prepareStatement(strSql);
			rs = ps.executeQuery();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return rs;
	}
	
	public Statement getStmt() {
		return stmt;
	}

	public Connection getConn() {
		return conn;
	}
	
}
