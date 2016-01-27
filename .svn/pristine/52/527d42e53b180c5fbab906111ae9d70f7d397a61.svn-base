package com.inveno.cps.workflow;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractState implements State {
	//包含的动作
	private Set<Action> set = new HashSet<Action>();
	//状态值
	private int value;
	//状态名称
	private String name;
	//状态类型：1，开始状态；2，普通状态；3，正常结束状态；4，异常结束状态
	private int type;
	
	public Set<Action> getSet() {
		return set;
	}

	public void setSet(Set<Action> set) {
		this.set = set;
	}

	private boolean current = false;
	
	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	public int getType() {
		return type;
	}
	
	protected void setType(int type) {
		this.type = type;
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

	public void addAction(Action action) {
		int curValue = action.getValue();
		boolean flag = true;
		for(Action tempAction:set) {
			int tempValue = tempAction.getValue();
			if(tempValue==curValue) {
				flag = false;
				break;
			}
		}
		if(flag) {
			set.add(action);
		}else {
			throw new RuntimeException("addAction方法：已存在value为"+curValue+"的动作");
		}
	}

	public void removeAction(Action action) {
		set.remove(action);
	}
	
	public void updateAction(Action action) {
		int curValue = action.getValue();
		Action actionInSet = null;
		for(Action tempAction:set) {
			int tempValue = tempAction.getValue();
			if(tempValue==curValue) {
				actionInSet = tempAction;
				break;
			}
		}
		if(actionInSet!=null) {
			set.remove(actionInSet);
			set.add(action);
		}else {
			throw new RuntimeException("updateAction方法：不存在value为"+curValue+"的动作");
		}
	}
	
	public Action getAction(int i) {
		Action actionInSet = null;
		for(Action tempAction:set) {
			int tempValue = tempAction.getValue();
			if(tempValue==i) {
				actionInSet = tempAction;
				break;
			}
		}
		return actionInSet;
	}
	
	public State goNext(Action action){
		this.setCurrent(false);
		State nextState = action.getNextState();
		nextState.setCurrent(true);
		//数据库持久化
		return nextState;
	}
	
	public State getNext(Action action) {
		return action.getNextState();
	}
	
}
