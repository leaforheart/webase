package com.inveno.cps.workflow;

public class BadEndState extends AbstractState {
	public BadEndState() {
		this.setType(StateType.BadEnd.getStateType());
	}
}
