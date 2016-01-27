package com.inveno.cps.authority.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.inveno.base.BaseModel;

/**
 * 用户角色关联表
 * 
 * 2011-4-1
 * 
 * @author yaoyuan
 */
@Entity
@Table(name = "t_userrole")
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserRole extends BaseModel {

	private static final long serialVersionUID = 1L;

	/** 主键 */
	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "userroleGenerate", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "userroleGenerate", strategy = "native")
	private String id;

	/** 角色名称 */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "role_id",insertable=false,updatable=false)
	private Role role;

	/** 权限菜单id */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "user_id",insertable=false,updatable=false)
	private User user;
	
	/** 角色名称 */
	@Column(name = "role_id")
	private String roleId;

	/** 员工id */
	@Column(name = "user_id")
	private String userId;
	

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	

	

}
