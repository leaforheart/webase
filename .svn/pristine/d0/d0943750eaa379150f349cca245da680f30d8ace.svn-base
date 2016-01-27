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
 * 角色权限关联表
 * 
 * 2011-4-1
 * 
 * @author yaoyuan
 */
@Entity
@Table(name = "t_rolepermission")
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RolePermission extends BaseModel {

	private static final long serialVersionUID = 1L;

	/** 主键 */
	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "rolepermissionGenerate", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "rolepermissionGenerate", strategy = "native")
	private String id;

	/** 角色名称 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "role_id",insertable=false,updatable=false)
	private Role role;

	/** 权限菜单id */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "permission_id",insertable=false,updatable=false)
	private Permission permission;
	
	/** 角色名称 */
	@Column(name = "role_id")
	private String roleId;

	/** 权限菜单id */
	@Column(name = "permission_id")
	private String permissionId;
	
	//项目名称
	@Column(name = "project")
	private String project;
	

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
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

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	

}
