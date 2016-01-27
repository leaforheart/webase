package com.inveno.cps.workflow;

import java.util.Set;

public interface State {
	/**
	 * 添加动作
	 * @param action
	 */
	void addAction(Action action);
	/**
	 * 删除动作
	 * @param action
	 */
	void removeAction(Action action);
	/**
	 * 更新动作
	 * @param action
	 */
	void updateAction(Action action);
	/**
	 * 获取动作
	 * @param i
	 * @return
	 */
	Action getAction(int i);
	/**
	 * 根据动作返回下一个状态，并改变当前状态值，如果没有定义这个动作的下一个状态，返回null
	 * @param action
	 * @return
	 */
	State goNext(Action action);
	/**
	 * 根据动作返回下一个状态，如果没有定义这个动作的下一个状态，返回null
	 * @param action
	 * @return
	 */
	State getNext(Action action);
	/**
	 * 获取状态类型
	 * @return
	 */
	int getType();
	/**
	 * 获取状态值
	 * @return
	 */
	int getValue();
	/**
	 * 设置状态值
	 * @param value
	 */
	void setValue(int value);
	/**
	 * 获取状态名称
	 * @return
	 */
	String getName();
	/**
	 * 设置状态名称
	 * @param name
	 */
	void setName(String name);
	/**
	 * 判断是不是当前状态
	 * @return
	 */
	boolean isCurrent();
	/**
	 * 设置当前状态
	 * @param current
	 */
	void setCurrent(boolean current);
	/**
	 * 获取动作集合
	 * @return
	 */
	Set<Action> getSet();
	/**
	 * 设置动作集合
	 * @param set
	 */
	void setSet(Set<Action> set);
	
	
	
}
