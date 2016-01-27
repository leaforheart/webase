package com.inveno.cps.authority.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.inveno.base.BaseModel;

/**
 * 角色表
 * 
 * 2011-3-30
 * 
 * @author yaoyuan
 */
@Entity
@Table(name = "t_role")
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.hibernate.annotations.Proxy(lazy = false)
public class Role extends BaseModel {

	private static final long serialVersionUID = 1L;

	/** 主键 */
	@Id
	@Column(name = "role_id")
	@GeneratedValue(generator = "roleGenerate", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "roleGenerate", strategy = "native")
	private String id;
	
	/**唯一的角色代码*/
	@Column(name="rolecode")
	private String roleCode;

	/** 角色名称 */
	@Column(name = "rolename")
	private String roleName;

	/** 备注 */
	@Column(name = "memo")
	private String memo;

	/** 创建时间 */
	@Column(name = "create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	/** 修改时间 */
	@Column(name = "last_upd_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdTime;
	
	/**
	 * 选中1，未选中0
	 */
	@Transient
	private String checkbox;
	

	public String getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(String checkbox) {
		this.checkbox = checkbox;
	}

	
	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdTime() {
		return lastUpdTime;
	}

	public void setLastUpdTime(Date lastUpdTime) {
		this.lastUpdTime = lastUpdTime;
	}

	


}
