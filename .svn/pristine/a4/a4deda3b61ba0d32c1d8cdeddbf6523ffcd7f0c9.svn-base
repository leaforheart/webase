package com.inveno.cps.review.service;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import com.inveno.cps.authority.model.UserRole;
import com.inveno.cps.common.baseclass.AbstractBaseService;
import com.inveno.cps.common.util.Constants;
import com.inveno.cps.common.util.FileByteBuffUtil;
import com.inveno.cps.common.util.FileUploadUtil;
import com.inveno.cps.review.dao.ReviewDao;
import com.inveno.cps.review.model.ActionModel;
import com.inveno.cps.review.model.BussinessTypeModel;
import com.inveno.cps.review.model.BussinessWorkflow;
import com.inveno.cps.review.model.BussinessWorkflowLog;
import com.inveno.cps.review.model.StateModel;
import com.inveno.cps.review.model.WorkFlowModel;
import com.inveno.cps.review.thrift.ReviewService;
import com.inveno.cps.workflow.Action;
import com.inveno.cps.workflow.BadEndState;
import com.inveno.cps.workflow.GeneralState;
import com.inveno.cps.workflow.GoodEndState;
import com.inveno.cps.workflow.StartState;
import com.inveno.cps.workflow.State;
import com.inveno.cps.workflow.StateType;
import com.inveno.cps.workflow.WorkFlow;
import com.inveno.util.DateUtil;
import com.inveno.util.JsonUtil;
import com.inveno.util.StringUtil;
/**
 * 工作流对外接口类
 * @author XYL
 *
 */
public class ReviewServiceImpl extends AbstractBaseService implements ReviewService.Iface {
	private Logger log = Logger.getLogger(ReviewServiceImpl.class);
	
	private ReviewDao reviewDao;
	
	public ReviewDao getReviewDao() {
		return reviewDao;
	}

	public void setReviewDao(ReviewDao reviewDao) {
		this.reviewDao = reviewDao;
	}
	/**
	 * 根据远程传输过来的xml文件，添加工作流
	 * 
	 */
	@Override
	public Map<String, String> addWorkFlow(ByteBuffer file, String filename) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			if(file==null) {
				map.put(Constants.RETURN_CODE, "-1"); //上传文件为空
				return map;
			}
			if(file.capacity()>2048*1024) {
				map.put(Constants.RETURN_CODE, "-2"); //上传的文件不能超过2M
				return map;
			}
			if(!".xml".equals(filename.substring(filename.lastIndexOf(".")))) {
				map.put(Constants.RETURN_CODE, "-3");//文件后缀只能是.xml
				return map;
			}
			
			String path = uploadWorkFlow(file,filename);
			//把xml文件内容存到java对象
			WorkFlow wf = xmlToWorkFlow(path);
			
			//检验数据库中是否有相同的工作流
			String name = wf.getName();
			List<String> parameters = new ArrayList<String>();
			parameters.add(name);
			List<WorkFlowModel> list = reviewDao.findByHql("from WorkFlowModel where name=?", parameters);
			if(list.size()>0) {
				map.put(Constants.RETURN_CODE, "-4");//已存在相同名称的工作流
				return map;
			}
			
			//检验工作流的合法性
			int result = WorkFlow.check(wf);
			
			if(result!=0) {
				map.put(Constants.RETURN_CODE, String.valueOf(result));
				return map;
			}
			//将java对象写入到数据库
			workFlowToDB(wf);
			
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	/**
	 * 把工作流存储到数据库
	 * @param wf
	 */
	private void workFlowToDB(WorkFlow wf) {
		try {
			String wfid = saveWorkFlow(wf);
			
			saveStateAndAction(wf,wfid);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}
		
	}
	
	private String saveWorkFlow(WorkFlow wf) {
		WorkFlowModel wfm = new WorkFlowModel();
		wfm.setName(wf.getName());
		wfm.setXmlPath(wf.getName());
		wfm.setCreateTime(new Date());
		wfm.setLastUpdateTime(new Date());
		reviewDao.save(wfm);
		String wfid = wfm.getId();
		return wfid;
	}
	
	private void saveStateAndAction(WorkFlow wf,String wfid) {
		Set<State> stateSet = wf.getSet();
		for(State state:stateSet) {
			StateModel sm = new StateModel();
			sm.setName(state.getName());
			sm.setType(String.valueOf(state.getType()));
			sm.setValue(String.valueOf(state.getValue()));
			sm.setWorkflow_id(wfid);
			sm.setCreateTime(new Date());
			sm.setLastUpdateTime(new Date());
			reviewDao.save(sm);
			String sid = sm.getId();
			
			Set<Action> actionSet = state.getSet();
			for(Action action:actionSet) {
				ActionModel am = new ActionModel();
				am.setActorId(action.getActorId());
				am.setActorType(String.valueOf(action.getActorType()));
				am.setCurStateId(sid);
				am.setNextStateId(String.valueOf(action.getNextStateId()));
				am.setValue(String.valueOf(action.getValue()));
				am.setCreateTime(new Date());
				am.setLastUpdateTime(new Date());
				reviewDao.save(am);
			}
		}
	}
	
	/***
	 * 把xml描述转化为java对象
	 * @param path
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private WorkFlow xmlToWorkFlow(String path) {
		WorkFlow wf = WorkFlow.getInstance();
		try {
			Document doc = FileUploadUtil.getDoc(path);
			Element root = doc.getRootElement();
			String rootName = root.getName();
			if(!"workflow".equals(rootName)) {
				throw new RuntimeException("xml根节点应该是workflow，而不是"+rootName);
			}
			Attribute aname = root.attribute("name");
			if(aname==null) {
				throw new RuntimeException("workflow必须有name属性");
			}
			String name = aname.getValue();
			wf.setName(name);
			wf.setXmlPath(path);
			Iterator<Element> iterator = root.elementIterator();
			while(iterator.hasNext()) {
				Element nextElement = iterator.next();
				String nextName = nextElement.getName();
				if(!"state".equals(nextName)) {
					throw new RuntimeException("workflow子元素只能是state，而不是"+nextName);
				}
				Attribute aNextValue = root.attribute("value");
				if(aNextValue==null) {
					throw new RuntimeException("state必须有value属性");
				}
				Attribute aNextName = root.attribute("name");
				if(aNextName==null) {
					throw new RuntimeException("state必须有name属性");
				}
				Attribute aNextType = root.attribute("type");
				if(aNextType==null) {
					throw new RuntimeException("state必须有type属性");
				}
				String sNextValue = aNextValue.getValue();
				String sNextName = aNextName.getValue();
				String sNextType = aNextType.getValue();
				State state = null;
				if(StateType.Start.getStateType()==Integer.parseInt(sNextType)) {
					state = new StartState();
				}else if(StateType.General.getStateType()==Integer.parseInt(sNextType)) {
					state = new GeneralState();
				}else if(StateType.GoodEnd.getStateType()==Integer.parseInt(sNextType)) {
					state = new GoodEndState();
				}else if(StateType.BadEnd.getStateType()==Integer.parseInt(sNextType)) {
					state = new BadEndState();
				}
				if(state == null) {
					throw new RuntimeException("state的type属性值不合法："+sNextType);
				}
				state.setValue(Integer.parseInt(sNextValue));
				state.setName(sNextName);
				wf.addState(state);
				if(StateType.GoodEnd.getStateType()!=Integer.parseInt(sNextType)&&StateType.BadEnd.getStateType()!=Integer.parseInt(sNextType)) {
					Iterator<Element> nextIterator = nextElement.elementIterator();
					while (nextIterator.hasNext()) {
						Element nextTwoElement = nextIterator.next();
						String nextTwoName = nextTwoElement.getName();
						if(!"action".equals(nextTwoName)) {
							throw new RuntimeException("state子元素只能是action，而不是"+nextName);
						}
						Attribute aNextTwoValue = root.attribute("value");
						if(aNextTwoValue==null) {
							throw new RuntimeException("action必须有value属性");
						}
						Attribute aNextTwoName = root.attribute("name");
						if(aNextTwoName==null) {
							throw new RuntimeException("action必须有name属性");
						}
						Attribute aNextTwoActorId = root.attribute("actorId");
						if(aNextTwoActorId==null) {
							throw new RuntimeException("action必须有actorId属性");
						}
						Attribute aNextTwoActorType = root.attribute("actorType");
						if(aNextTwoActorType==null) {
							throw new RuntimeException("action必须有actorType属性");
						}
						Attribute aNextTwoNextState = root.attribute("nextState");
						if(aNextTwoNextState==null) {
							throw new RuntimeException("action必须有nextState属性");
						}
						Action action = new Action();
						action.setActorId(aNextTwoActorId.getValue());
						action.setActorType(Integer.parseInt(aNextTwoActorType.getValue()));
						action.setName(aNextTwoName.getValue());
						action.setValue(Integer.parseInt(aNextTwoValue.getValue()));
						action.setNextStateId(Integer.parseInt(aNextTwoNextState.getValue()));
						state.addAction(action);
					}
				}
				
			}
			Set<State> setState = wf.getSet();
			for(State state:setState) {
				if(StateType.GoodEnd.getStateType()!=state.getType()&&StateType.BadEnd.getStateType()!=state.getType()) { 
					Set<Action> setAction = state.getSet();
					for(Action action:setAction) {
						int nextStateId = action.getNextStateId();
						for(State stateTmp:setState) {
							if(stateTmp.getValue()==nextStateId) {
								action.setNextState(stateTmp);
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}
		
		return wf;
	}
	
	private String uploadWorkFlow(ByteBuffer file, String filename) throws Exception {
		filename = FileUploadUtil.getUuidName(filename);
		String dir = System.getProperty("user.home")+System.getProperty("file.separator")+"upload"+System.getProperty("file.separator")+"workflowxml"+System.getProperty("file.separator")+DateUtil.formatDate("yyyyMMdd", new Date())+System.getProperty("file.separator");
		FileByteBuffUtil.byteBufferToFile(file, dir,filename);
		return dir + filename;
	}

	@Override
	public Map<String, String> delWorkFlow(String id) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			boolean isFlowing = checkFlowing(id);
			if(isFlowing) {
				map.put(Constants.RETURN_CODE, "-1");//还有业务在使用这个工作流，不能删除
				return map;
			}
			
			WorkFlowModel wfm = reviewDao.findById(id, WorkFlowModel.class);
			String wfmid = wfm.getId();
			deleteStateAndAction(wfmid);
			reviewDao.delete(wfm);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	
	private void deleteStateAndAction(String wfmid) {
		List<String> parameters = new ArrayList<String>();
		parameters.add(wfmid);
		List<StateModel> stateList = reviewDao.findByHql("from StateModel where workflowId=?", parameters);
		for(StateModel sm:stateList) {
			String smid = sm.getId();
			List<String> parameter1 = new ArrayList<String>();
			parameter1.add(smid);
			reviewDao.excuteHql("delete from ActionModel where curStateId=?", parameter1);
			reviewDao.delete(sm);
		}
	}
	
	private boolean checkFlowing(String id) {
		boolean flag = false;
		List<String> parameters = new ArrayList<String>();
		parameters.add(id);
		parameters.add(String.valueOf(StateType.BadEnd.getStateType()));
		parameters.add(String.valueOf(StateType.GoodEnd.getStateType()));
		List<Object> list = reviewDao.findByHql("from BussinessTypeModel as btm,BussinessWorkflow bwf,StateModel sm where btm.id=bwf.bussinessTypeId and bwf.curState=sm.id and btm.workflowId=? and sm.type<>? and sm.type<>?", parameters);
		if(list.size()>0) {
			flag = true;
		}
		return flag;
	}

	@Override
	public Map<String, String> updWorkFlow(ByteBuffer file, String filename) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			if(file==null) {
				map.put(Constants.RETURN_CODE, "-1"); //上传文件为空
				return map;
			}
			if(file.capacity()>2048*1024) {
				map.put(Constants.RETURN_CODE, "-2"); //上传的文件不能超过2M
				return map;
			}
			if(!".xml".equals(filename.substring(filename.lastIndexOf(".")))) {
				map.put(Constants.RETURN_CODE, "-3");//文件后缀只能是.xml
				return map;
			}
			String path = uploadWorkFlow(file,filename);
			//把xml文件内容存到java对象
			WorkFlow wf = xmlToWorkFlow(path);
			//检验工作流的合法性
			int result = WorkFlow.check(wf);
			if(result!=0) {
				map.put(Constants.RETURN_CODE, String.valueOf(result));
				return map;
			}
			
			String name = wf.getName();
			List<String> parameters = new ArrayList<String>();
			parameters.add(name);
			List<WorkFlowModel> list = reviewDao.findByHql("from WorkFlowModel where name=?", parameters);
			if(list.size()==0) {
				map.put(Constants.RETURN_CODE, "-4");//不存在此工作流
				return map;
			}
			String id = list.get(0).getId();
			
			boolean isFlowing = checkFlowing(id);
			if(isFlowing) {
				map.put(Constants.RETURN_CODE, "-5");//还有业务在使用这个工作流，不能删除
				return map;
			}
			
			updateWorkflow(wf,list.get(0));
			
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	
	private void updateWorkflow(WorkFlow wf,WorkFlowModel wfm) {
		wfm.setLastUpdateTime(new Date());
		wfm.setName(wf.getName());
		wfm.setXmlPath(wf.getXmlPath());
		String wfmid = wfm.getId();
		reviewDao.update(wfm);
		
		this.deleteStateAndAction(wfmid);
		
		this.saveStateAndAction(wf, wfmid);
	}

	@Override
	public Map<String, String> getWorkFlow(String id) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			WorkFlowModel wfm = reviewDao.findById(id, WorkFlowModel.class);
			if(wfm==null) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			String path = wfm.getXmlPath();
			String contents = FileByteBuffUtil.getFileContents(path);
			wfm.setContents(contents);
			String data = JsonUtil.getJsonStrByConfigFromObj(wfm);
			map.put(Constants.RETURN_DATA, data);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}

	@Override
	public Map<String, String> queWorkFlow(Map<String, String> queryMap) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			List<String> parameters = new ArrayList<String>();
			String hql = "from WorkFlowModel where 1=1";
			
			String name = queryMap.get("name");
			if(StringUtil.isNotEmpty(name)) {
				parameters.add("%"+name+"%");
				hql += "and name like ?";
			}
			List<Object> list = reviewDao.findByHql(hql, parameters);
			map.put(Constants.RETURN_DATA, JsonUtil.getJsonStrFromList(list));
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	/**
	 * 添加业务类型
	 */
	@Override
	public Map<String, String> addBussinessType(String workflowid, String name,String des) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			BussinessTypeModel btm = new BussinessTypeModel();
			//1.检查工作流是否存在
			if(StringUtil.isEmpty(workflowid)) {
				map.put(Constants.RETURN_CODE, "-1");//不存在相应的工作流
				return map;
			}
			WorkFlowModel wfm = reviewDao.findById(workflowid, WorkFlowModel.class);
			if(wfm==null) {
				map.put(Constants.RETURN_CODE, "-2");//不存在相应的工作流
				return map;
			}
			//2.检查业务名称是否重复
			List<String> parameters = new ArrayList<String>();
			parameters.add(name);
			List<BussinessTypeModel> list = reviewDao.findByHql("from BussinessTypeModel where name=?", parameters);
			if(list!=null&&list.size()>0) {
				map.put(Constants.RETURN_CODE, "-3");//已存在相同名称的业务
				return map;
			}
			
			btm.setWorkflowId(workflowid);
			btm.setName(name);
			if(des!=null) {
				btm.setDes(des);
			}
			btm.setCreateTime(new Date());
			btm.setLastUpdateTime(new Date());
			reviewDao.save(btm);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}

	@Override
	public Map<String, String> delBussinessType(String id) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			boolean flag = checkBussinessing(id);
			if(flag) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			List<String> parameter = new ArrayList<String>();
			parameter.add(id);
			reviewDao.excuteHql("delete from BussinessTypeModel where id=?", parameter);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	
	private boolean checkBussinessing(String id) {
		boolean flag = false;
		
		List<String> parameters = new ArrayList<String>();
		parameters.add(id);
		parameters.add(String.valueOf(StateType.BadEnd.getStateType()));
		parameters.add(String.valueOf(StateType.GoodEnd.getStateType()));
		List<Object> list = reviewDao.findByHql("from BussinessWorkflow bwf,StateModel sm where bwf.curState=sm.id and bwf.bussinessTypeId=? and sm.type<>? and sm.type<>?", parameters);
		if(list.size()>0) {
			flag = true;
		}
		
		return flag;
	}

	@Override
	public Map<String, String> updBussinessType(String id, Map<String,String> updateMap) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			boolean flag = checkBussinessing(id);
			if(flag) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			BussinessTypeModel btm = reviewDao.findById(id, BussinessTypeModel.class);
			
			String workflowid = updateMap.get("workflowid");
			String des = updateMap.get("des");
			String name = updateMap.get("name");
			btm.setLastUpdateTime(new Date());
			if(StringUtil.isNotEmpty(des)) {
				btm.setDes(des);
			}
			if(StringUtil.isNotEmpty(name)) {
				btm.setName(name);
			}
			if(StringUtil.isNotEmpty(workflowid)) {
				btm.setWorkflowId(workflowid);
			}
			reviewDao.update(btm);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}

	@Override
	public Map<String, String> getBussinessType(String id) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			BussinessTypeModel btm = reviewDao.findById(id, BussinessTypeModel.class);
			map.put(Constants.RETURN_DATA, JsonUtil.getJsonStrFromObj(btm));
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}

	@Override
	public Map<String, String> queBussinessType(Map<String, String> queryMap) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			List<String> parameters = new ArrayList<String>();
			String hql = "from BussinessTypeModel where 1=1";
			
			String name = queryMap.get("name");
			if(StringUtil.isNotEmpty(name)) {
				parameters.add("%"+name+"%");
				hql += "and name like ?";
			}
			List<Object> list = reviewDao.findByHql(hql, parameters);
			map.put(Constants.RETURN_DATA, JsonUtil.getJsonStrFromList(list));
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	//根据业务类型，找到工作流，根据工作流，找到所有状态
	@Override
	public Map<String, String> queState(String bussinessTypeId) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			BussinessTypeModel btm = reviewDao.findById(bussinessTypeId,BussinessTypeModel.class);
			if(btm==null) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			String workFlowId = btm.getWorkflowId();
			List<String> parameters = new ArrayList<String>();
			parameters.add(workFlowId);
			List<Object> list = reviewDao.findByHql("from StateModel where workflow_id=?", parameters);
			
			map.put(Constants.RETURN_DATA, JsonUtil.getJsonStrFromList(list));
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	/**
	 * 启动一个工作流实例
	 */
	@Override
	public Map<String, String> startWorkFlow(String userid,String bussinesstypeid,String bussinessid) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			BussinessTypeModel btm = reviewDao.findById(bussinesstypeid,BussinessTypeModel.class);
			if(btm==null) {
				map.put(Constants.RETURN_CODE, "-1");//不存在该业务类型
				return map;
			}
			String workFlowId = btm.getWorkflowId();
			if(StringUtil.isEmpty(workFlowId)) {
				map.put(Constants.RETURN_CODE, "-2");//该业务类型没有设置对应的工作流
				return map;
			}
			
			if(StringUtil.isEmpty(userid)) {
				map.put(Constants.RETURN_CODE, "-3");//业务操作者用户id获取失败
				return map;
			}
			List<String> parameters = new ArrayList<String>();
			parameters.add(workFlowId);
			parameters.add(String.valueOf(StateType.Start.getStateType()));
			List<StateModel> list = reviewDao.findByHql("from StateModel where workflow_id=? and type=?", parameters);
			if(list==null||list.size()==0) {
				map.put(Constants.RETURN_CODE, "-4");//没有对应的工作流
				return map;
			}
			
			BussinessWorkflow bw = new BussinessWorkflow();
			bw.setUserId(userid);
			bw.setBussinessTypeId(bussinesstypeid);
			bw.setBussinessId(bussinessid);
			bw.setCurState(list.get(0).getId());
			bw.setOperTime("1");
			bw.setCreateTime(new Date());
			reviewDao.save(bw);
			
			BussinessWorkflowLog bwl = new BussinessWorkflowLog();
			bwl.setUserId(userid);
			bwl.setBussinessTypeId(bussinesstypeid);
			bwl.setBussinessId(bussinessid);
			bwl.setCurState(list.get(0).getId());
			bwl.setOperTime("1");
			bwl.setCreateTime(new Date());
			reviewDao.save(bwl);
			
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}

	@Override
	public Map<String, String> queBussinessWorkflow(String userId,String bussinessTypeId,String stateId) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {

			if(StringUtil.isEmpty(userId)) {
				map.put(Constants.RETURN_CODE, "-1");//请求参数中，用户id不能为空
				return map;
			}
			List<String> parameters = new ArrayList<String>();
			parameters.add(userId);
			List<UserRole> list = reviewDao.findByHql("from UserRole where userId=?", parameters);
			Set<ActionModel> set = new HashSet<ActionModel>();
			for(UserRole ur:list) {
				String roleId = ur.getRoleId();
				List<String> parameters1 = new ArrayList<String>();
				parameters1.add(roleId);
				parameters1.add(Constants.ROLE_CHECK);
				List<ActionModel> list1 = reviewDao.findByHql("from ActionModel actorId=? and actorType=?", parameters1);
				for(ActionModel am:list1) {
					set.add(am);
				}
			}
			List<String> parameters2 = new ArrayList<String>();
			parameters2.add(userId);
			parameters2.add(Constants.USER_CHECK);
			List<ActionModel> list2 = reviewDao.findByHql("from ActionModel actorId=? and actorType=?", parameters2);
			for(ActionModel am:list2) {
				set.add(am);
			}
			
			Set<BussinessWorkflow> set3 = new HashSet<BussinessWorkflow>();
			for(ActionModel am:set) {
				String curStateId = am.getCurStateId();
				List<String> parameters3 = new ArrayList<String>();
				parameters3.add(curStateId);
				List<BussinessWorkflow> list3 = reviewDao.findByHql("from BussinessWorkflow where curState=?", parameters3);
				StateModel sm = reviewDao.findById(curStateId,StateModel.class);
				Set<ActionModel> set4 = new HashSet<ActionModel>();
				List<String> parameters4 = new ArrayList<String>();
				parameters4.add(curStateId);
				List<ActionModel> list4 = reviewDao.findByHql("from ActionModel where curStateId=?", parameters4);
				for(ActionModel am2:list4) {
					set4.add(am2);
				}
				set4.retainAll(set);//求交集，获取有权限的动作
				if(set4!=null&&set4.size()>0) {
					for(BussinessWorkflow bwf:list3) {
						bwf.setCurStateModel(sm);
						bwf.setActionSet(set4);
						set3.add(bwf);
					}
				}
			}
			
			List<String> parameters5 = new ArrayList<String>();
			parameters5.add("-1");
			parameters5.add(Constants.USER_CHECK);
			List<ActionModel> list5 = reviewDao.findByHql("from ActionModel actorId=? and actorType=?", parameters5);
			Set<BussinessWorkflow> set5 = new HashSet<BussinessWorkflow>();
			for(ActionModel am:list5) {
				String curStateId = am.getCurStateId();
				StateModel sm = reviewDao.findById(curStateId,StateModel.class);
				Set<ActionModel> set7 = new HashSet<ActionModel>();
				List<String> parameters7 = new ArrayList<String>();
				parameters7.add(curStateId);
				List<ActionModel> list4 = reviewDao.findByHql("from ActionModel where curStateId=?", parameters7);
				for(ActionModel am2:list4) {
					set7.add(am2);
				}
				List<String> parameters6 = new ArrayList<String>();
				parameters6.add(curStateId);
				parameters6.add(userId);
				List<BussinessWorkflow> list6 = reviewDao.findByHql("from BussinessWorkflow where curState=? and userId=?", parameters6);
				for(BussinessWorkflow bwf:list6) {
					bwf.setCurStateModel(sm);
					bwf.setActionSet(set7);
					set5.add(bwf);
				}
			}
			
			set3.addAll(set5);
			
			List<Object> dataList = new ArrayList<Object>();
			for(BussinessWorkflow bwf:set3) {
				dataList.add(bwf);
			}
			
			//过滤
			if(!Constants.INIT_ID.equals(bussinessTypeId)&&StringUtil.isNotEmpty(bussinessTypeId)) {
				List<Object> tempList = new ArrayList<Object>();
 				for(Object obj:dataList) {
					BussinessWorkflow bwf = (BussinessWorkflow)obj;
					if(!bussinessTypeId.equals(bwf.getBussinessTypeId())) {
						tempList.add(obj);
					}
				}
 				dataList.removeAll(tempList);
 				if(!Constants.INIT_ID.equals(stateId)&&StringUtil.isNotEmpty(stateId)) {
 					List<Object> tempList1 = new ArrayList<Object>();
 	 				for(Object obj:dataList) {
 						BussinessWorkflow bwf = (BussinessWorkflow)obj;
 						if(!stateId.equals(bwf.getCurState())) {
 							tempList1.add(obj);
 						}
 					}
 	 				dataList.removeAll(tempList1);
 				}
			}
			
			map.put(Constants.RETURN_DATA, JsonUtil.getJsonStrFromList(dataList));
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	/**
	 * 执行某动作
	 */
	@Override
	public Map<String, String> excuteAction(String bussinesstypeid,String bussinessid,String actionid,String reason) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			List<String> parameters1 = new ArrayList<String>();
			parameters1.add(bussinesstypeid);
			parameters1.add(bussinessid);
			List<BussinessWorkflow> list1 = reviewDao.findByHql("from BussinessWorkflow where bussiness_type_id=? and bussiness_id=?", parameters1);
			if(list1==null||list1.size()==0) {
				map.put(Constants.RETURN_CODE, "-1");//该业务没有启动工作流程
				return map;
			}
			BussinessWorkflow bwf = list1.get(0);
			String curStateId = bwf.getCurState();
			
			List<String> parameters2 = new ArrayList<String>();
			parameters2.add(curStateId);
			List<ActionModel> list2 = reviewDao.findByHql("from ActionModel where curStateId=?", parameters2);
			if(list2==null||list2.size()==0||StringUtil.isEmpty(actionid)) {
				map.put(Constants.RETURN_CODE, "-2");//当前状态不存在该动作
				return map;
			}
			boolean flag = false;
			for(ActionModel am:list2) {
				if(actionid.equals(am.getId())) {
					flag = true;
					break;
				}
			}
			if(!flag) {
				map.put(Constants.RETURN_CODE, "-2");//当前状态不存在该动作
				return map;
			}
			
			String operTime = bwf.getOperTime();
			ActionModel am = reviewDao.findById(actionid, ActionModel.class);
			if(am==null) {
				map.put(Constants.RETURN_CODE, "-3");//不存在该动作
				return map;
			}
			
			BussinessWorkflowLog bwfl = new BussinessWorkflowLog();
			bwfl.setUserId(bwf.getUserId());
			bwfl.setBussinessTypeId(bussinesstypeid);
			bwfl.setBussinessId(bussinessid);
			bwfl.setAction(actionid);
			if(!StringUtil.isEmpty(reason)) {
				bwfl.setReason(reason);
			}
			bwfl.setCreateTime(new Date());
			bwfl.setLastState(curStateId);
			bwfl.setCurState(am.getNextStateId());
			bwfl.setOperTime(String.valueOf(Integer.parseInt(operTime)+1));
			reviewDao.save(bwfl);
			
			bwf.setAction(actionid);
			bwf.setCreateTime(new Date());
			bwf.setCurState(am.getNextStateId());
			bwf.setLastState(curStateId);
			bwf.setOperTime(String.valueOf(Integer.parseInt(operTime)+1));
			if(!StringUtil.isEmpty(reason)) {
				bwf.setReason(reason);
			}
			reviewDao.update(bwf);
			
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}

	@Override
	public Map<String, String> queBussinessWorkflowLog(String bussinesstypeid,String bussinessid) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			List<String> parameters = new ArrayList<String>();
			parameters.add(bussinesstypeid);
			parameters.add(bussinessid);
			List<Object> list = reviewDao.findByHql("from BussinessWorkflowLog where bussiness_type_id=? and bussiness_id=?", parameters);
			for(Object obj:list) {
				BussinessWorkflowLog bwfl = (BussinessWorkflowLog)obj;
				String curStateId = bwfl.getCurState();
				String lastStateId = bwfl.getLastState();
				String actionId = bwfl.getAction();
				StateModel curState = reviewDao.findById(curStateId,StateModel.class);
				StateModel lastState = reviewDao.findById(lastStateId, StateModel.class);
				ActionModel actionModel = reviewDao.findById(actionId, ActionModel.class);
				bwfl.setCurStateModel(curState);
				bwfl.setLastStateModel(lastState);
				bwfl.setActionModel(actionModel);
			}
			String data = JsonUtil.getJsonStrFromList(list);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put(Constants.RETURN_DATA, data);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}

	@Override
	public void ping() {
		
	}
	
}
