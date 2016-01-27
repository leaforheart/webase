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
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
@Entity
@Table(name = "cps_bussiness_workflow_log")
@org.hibernate.annotations.Entity(dynamicInsert = true, dynamicUpdate = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BussinessWorkflowLog {
	@Id
	@Column(name = "id")
	@GeneratedValue(generator = "BussinessWorkflowLogGenerate", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "BussinessWorkflowLogGenerate", strategy = "native")
	private String id;
	
	@Column(name = "user_id")
	private String userId;
	
	@Column(name = "bussiness_type_id")
	private String bussinessTypeId;
	
	@Column(name = "bussiness_id")
	private String bussinessId;
	
	@Column(name = "last_state")
	private String lastState;
	
	@Column(name = "action")
	private String action;
	
	@Column(name = "cur_state")
	private String curState;
	
	@Column(name = "reason")
	private String reason;
	
	@Column(name = "oper_time")
	private String operTime;

	@Column(name = "create_time")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;
	
	@Transient
	private StateModel curStateModel;
	
	@Transient
	private StateModel lastStateModel;
	
	@Transient
	private ActionModel actionModel;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getBussinessTypeId() {
		return bussinessTypeId;
	}

	public void setBussinessTypeId(String bussinessTypeId) {
		this.bussinessTypeId = bussinessTypeId;
	}

	public String getBussinessId() {
		return bussinessId;
	}

	public void setBussinessId(String bussinessId) {
		this.bussinessId = bussinessId;
	}

	public String getLastState() {
		return lastState;
	}

	public void setLastState(String lastState) {
		this.lastState = lastState;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCurState() {
		return curState;
	}

	public void setCurState(String curState) {
		this.curState = curState;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getOperTime() {
		return operTime;
	}

	public void setOperTime(String operTime) {
		this.operTime = operTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public StateModel getCurStateModel() {
		return curStateModel;
	}

	public void setCurStateModel(StateModel curStateModel) {
		this.curStateModel = curStateModel;
	}

	public StateModel getLastStateModel() {
		return lastStateModel;
	}

	public void setLastStateModel(StateModel lastStateModel) {
		this.lastStateModel = lastStateModel;
	}

	public ActionModel getActionModel() {
		return actionModel;
	}

	public void setActionModel(ActionModel actionModel) {
		this.actionModel = actionModel;
	}

	
}
