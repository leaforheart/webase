package com.inveno.cps.authority.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.inveno.base.BaseModel;

@Entity
@Table(name = "t_permission_mapping")
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PermissionMapping extends BaseModel {
	private static final long serialVersionUID = -7394587402772226903L;
	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "permissionMappingGenerate", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "permissionMappingGenerate", strategy = "native")
	private String id;
	@Column(name = "old_permission")
	private String oldPermission;
	@Column(name = "new_permission")
	private String newPermission;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOldPermission() {
		return oldPermission;
	}
	public void setOldPermission(String oldPermission) {
		this.oldPermission = oldPermission;
	}
	public String getNewPermission() {
		return newPermission;
	}
	public void setNewPermission(String newPermission) {
		this.newPermission = newPermission;
	}
}
