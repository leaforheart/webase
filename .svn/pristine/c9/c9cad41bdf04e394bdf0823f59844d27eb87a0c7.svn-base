package com.inveno.cps.workflow;

import java.util.HashSet;
import java.util.Set;

import com.inveno.cps.common.util.Constants;
import com.inveno.util.StringUtil;
/**
 * 
 * @author XYL
 *
 */
public class WorkFlow {
	private WorkFlow(){}
	/**
	 * 工作流的状态集合
	 */
	Set<State> set = new HashSet<State>();
	
	private String name;
	
	private String xmlPath;
	
	public String getXmlPath() {
		return xmlPath;
	}
	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<State> getSet() {
		return set;
	}
	public void setSet(Set<State> set) {
		this.set = set;
	}
	/**
	 * 添加状态
	 * @param state
	 */
	public void addState(State state){
		int value = state.getValue();
		boolean flag = true;
		for(State tempState:set) {
			if(value==tempState.getValue()) {
				flag = false;
				break;
			}
		}
		if(flag) {
			set.add(state);
		} else {
			throw new RuntimeException("addState方法:已存在value值为"+value+"的状态");
		}
	}
	/**
	 * 删除状态
	 * @param state
	 */
	public void removeState(State state){
		set.remove(state);
	}
	/**
	 * 更新状态
	 * @param state
	 */
	public void updateState(State state){
		int i = state.getValue();
		State stateInSet = null;
		for(State tempState:set) {
			if(i==tempState.getValue()) {
				stateInSet = tempState;
				break;
			}
		}
		if(stateInSet!=null) {
			set.remove(stateInSet);
			set.add(state);
		}else {
			throw new RuntimeException("updateState方法：不存在value值为"+i+"的状态");
		}
	}
	/**
	 * 获取状态
	 * @param i
	 * @return
	 */
	public State getState(int i){
		State stateInSet = null;
		for(State tempState:set) {
			if(i==tempState.getValue()) {
				stateInSet = tempState;
				break;
			}
		}
		return stateInSet;
	}
	/**
	 * 获取工作流实例，空对象
	 * @return
	 */
	public static WorkFlow getInstance(){
		WorkFlow wf = new WorkFlow();
		return wf;
	}
	/**
	 * 检验工作流合法性
	 * @param wf
	 * @return
	 */
	public static int check(WorkFlow wf) {
		Set<State> stateSet = wf.getSet();
		State startState = null;
		int startInt = 0;
		int goodEndInt = 0;
		int badEndInt = 0;
		for(State state:stateSet) {
			if(state.getType()==StateType.Start.getStateType()) {
				startState = state;
				startInt++;
			}
			if(state.getType()==StateType.GoodEnd.getStateType()) {
				goodEndInt++;
			}
			if(state.getType()==StateType.BadEnd.getStateType()) {
				badEndInt++;
			}
		}
		if(startInt==0) {
			return Constants.NO_STARTSTATE;
		}else if(startInt>1) {
			return Constants.MUTI_STARTSTATE;
		}else if(goodEndInt==0) {
			return Constants.NO_GOODENDSTATE;
		}else if(goodEndInt>1) {
			return Constants.MUTI_GOODENDSTATE;
		}else if(badEndInt==0) {
			return Constants.NO_BADENDSTATE;
		}else if(badEndInt>1) {
			return Constants.MUTI_BADENDSTATE;
		}
		
		int result = flowCheck(stateSet,startState);
		
		if(result!=Constants.PASS) {
			return result;
		}
		 
		return Constants.PASS;
		
	}
	
	private static int flowCheck(Set<State> set,State state) {
		
		String stateName = state.getName();
		if(StringUtil.isEmpty(stateName)) {
			return Constants.STATE_NAME_EMPTY;
		}else if(stateName.length()>20) {
			return Constants.STATE_NAME_TOOLONG;
		}
		
		int type = state.getType();
		StateType[] st = StateType.values();
		boolean flag = false;
		for(StateType stateType:st) {
			if(type==stateType.getStateType()) {
				flag = true;
				break;
			}
		}
		if(!flag) {
			return Constants.STATE_TYPE_ERROR;
		}
		
		Set<Action> actionSet = state.getSet();
		for(Action action:actionSet) {
			if(StringUtil.isEmpty(action.getActorId())){
				return Constants.ACTION_NO_ACTOR;
			}
			if(action.getActorType()!=1&&action.getActorType()!=2) {
				return Constants.ACTION_ACTOR_TYPE_ERROR;
			}
			String actionName = action.getName();
			if(StringUtil.isEmpty(actionName)) {
				return Constants.ACTION_NAME_EMPTY;
			}else if(actionName.length()>20) {
				return Constants.ACTION_NAME_TOOLONG;
			}
			State next = state.getNext(action);
			if(!set.contains(next)&&(next.getType()!=StateType.GoodEnd.getStateType()||next.getType()!=StateType.BadEnd.getStateType())) {
				return Constants.NO_NEXT;
			}
			if(next.getType()!=StateType.GoodEnd.getStateType()&&next.getType()!=StateType.BadEnd.getStateType()) {
				flowCheck(set,next);
			}
		}
		return Constants.PASS;
	}
}
