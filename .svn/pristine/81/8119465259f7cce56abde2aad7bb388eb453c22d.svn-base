package com.inveno.cps.review.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

import com.inveno.base.BaseModel;

@Entity
@Table(name = "cps_action")
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ActionModel extends BaseModel {
	private static final long serialVersionUID = -3623092362171766970L;
	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "ActionModelGenerate", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "ActionModelGenerate", strategy = "native")
	private String id;
	
	@Column(name = "curstate_id")
	private String curStateId;
	
	@Column(name = "value")
	private String value;
	
	@Column(name = "reason")
	private String reason;
	
	@Column(name = "actor_id")
	private String actorId;
	
	@Column(name = "actor_type")
	private String actorType;
	
	@Column(name = "nextstate_id")
	private String nextStateId;
	
	@Column(name = "create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	@Column(name = "last_update_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCurStateId() {
		return curStateId;
	}

	public void setCurStateId(String curStateId) {
		this.curStateId = curStateId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getActorType() {
		return actorType;
	}

	public void setActorType(String actorType) {
		this.actorType = actorType;
	}

	public String getNextStateId() {
		return nextStateId;
	}

	public void setNextStateId(String nextStateId) {
		this.nextStateId = nextStateId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	
}
