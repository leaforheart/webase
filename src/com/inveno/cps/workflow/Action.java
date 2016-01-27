package com.inveno.cps.workflow;

public class Action {
	//动作值
	private int value;
	//动作名称
	private String name;
	//发起动作理由
	private String reason;
	//角色id或用户id
	private String actorId;
	//1角色2用户
	private int actorType;
	//发生动作以后的状态 
	private State nextState;
	//发生动作以后的状态id
	private int nextStateId;
	
	public int getNextStateId() {
		return nextStateId;
	}
	public void setNextStateId(int nextStateId) {
		this.nextStateId = nextStateId;
	}
	public String getActorId() {
		return actorId;
	}
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}
	public int getActorType() {
		return actorType;
	}
	public void setActorType(int actorType) {
		this.actorType = actorType;
	}
	public State getNextState() {
		return nextState;
	}
	public void setNextState(State nextState) {
		this.nextState = nextState;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
