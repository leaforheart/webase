package com.inveno.cps.workflow;

public enum StateType {
	Start(1),General(2),GoodEnd(3),BadEnd(4);
	private int stateType;
	
	public int getStateType() {
		return stateType;
	}

	public void setStateType(int stateType) {
		this.stateType = stateType;
	}

	private StateType(int i) {
		stateType = i;
	}
}
