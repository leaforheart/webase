namespace java com.inveno.cps.review.thrift

service ReviewService {
	//创建工作流,名称要唯一
	map<string,string> addWorkFlow(1:binary file,2:string filename),
	
	//删除工作流，全部执行中的业务置为异常结束状态
	map<string,string> delWorkFlow(1:string id),
	
	//更新工作流，全部执行中的业务置为开始状态
	map<string,string> updWorkFlow(1:binary file,2:string filename),
	
	//获取单个工作流
	map<string,string> getWorkFlow(1:string id),
	
	//获取所有工作流列表
	map<string,string> queWorkFlow(1:map<string,string> queryMap),
	
	//创建业务类型
	map<string,string> addBussinessType(1:string workflowid,2:string name,3:string des),
	
	//删除业务类型,所有执行中的业务实例置为异常结束状态
	map<string,string> delBussinessType(1:string id),
	
	//修改业务类型,如果修改了workflowid,所有执行中的业务实例置为开始状态
	map<string,string> updBussinessType(1:string id,2:map<string,string> updateMap),
	
	//获取单个业务类型
	map<string,string> getBussinessType(1:string id),
	
	//获取所有业务类型列表
	map<string,string> queBussinessType(1:map<string,string> queryMap),
	
	//根据业务类型id，获取状态列表
	map<string,string> queState(1:string bussinessTypeId),
	
	//启动工作流程实例
	map<string,string> startWorkFlow(1:string userid,2:string bussinesstypeid,3:string bussinessid),
	
	//根据用户id,工作流id,状态查询工作流程实例列表
	map<string,string> queBussinessWorkflow(1:string userid,2:string bussinesstypeid,3:string stateid),
	
	//执行动作
	map<string,string> excuteAction(1:string bussinesstypeid,2:string bussinessid,3:string actionid,4:string reason),
	
	//查询某工作流程实例历史记录
	map<string,string> queBussinessWorkflowLog(1:string bussinesstypeid,2:string bussinessid),
	
	//判断连接是否断开
	void ping()

}