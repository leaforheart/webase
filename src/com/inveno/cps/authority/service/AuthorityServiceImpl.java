package com.inveno.cps.authority.service;

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
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.inveno.cps.authority.dao.AuthorityDao;
import com.inveno.cps.authority.model.Catalog;
import com.inveno.cps.authority.model.Permission;
import com.inveno.cps.authority.model.PermissionConfigure;
import com.inveno.cps.authority.model.PermissionExclude;
import com.inveno.cps.authority.model.PermissionMapping;
import com.inveno.cps.authority.model.Role;
import com.inveno.cps.authority.model.RolePermission;
import com.inveno.cps.authority.model.User;
import com.inveno.cps.authority.model.UserRole;
import com.inveno.cps.common.baseclass.AbstractBaseService;
import com.inveno.cps.common.util.Constants;
import com.inveno.cps.common.util.FileByteBuffUtil;
import com.inveno.cps.common.util.FileUploadUtil;
import com.inveno.util.DateUtil;
import com.inveno.util.JsonUtil;
import com.inveno.util.StringUtil;
import com.inveno.cps.authority.thrift.AuthorityService;
/**
 * 账号权限管理服务类
 * 提供用户管理，角色管理，权限管理，权限认证功能；并提供远程调用接口
 * 
 * @author XYL
 * @email yilin.xiao@inveno.cn
 */
public class AuthorityServiceImpl extends AbstractBaseService implements AuthorityService.Iface {
	private Logger log = Logger.getLogger(AuthorityServiceImpl.class);
	
	private AuthorityDao authorityDao;

	public AuthorityDao getAuthorityDao() {
		return authorityDao;
	}

	public void setAuthorityDao(AuthorityDao authorityDao) {
		this.authorityDao = authorityDao;
	}
	
	/**
	 * @author XYL
	 * 上传权限配置文件到cps服务器
	 * 
	 */
	@Override
	public Map<String, String> uploadPermission(ByteBuffer file, String filename) {
		
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
			
			filename = FileUploadUtil.getUuidName(filename);
			String dir = System.getProperty("user.home")+System.getProperty("file.separator")+"upload"+System.getProperty("file.separator")+"permissionxml"+System.getProperty("file.separator")+DateUtil.formatDate("yyyyMMdd", new Date())+System.getProperty("file.separator");
			FileByteBuffUtil.byteBufferToFile(file, dir,filename);
			String path = dir + filename;
			
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put("path", path);
		}  catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		
		return map;
	}
	
	
	/**
	 * 一键导入所有权限
	 */
	@Override
	public Map<String, String> importPermission(String projectname,String xmlpath) {
		HashMap<String, String> map = new HashMap<String,String>();
		try {
			docToDB(xmlpath,projectname);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		} 
		return map;
	}
	/**
	 * 把xml文件持久化到数据库
	 * @param xmlPath
	 * @param project
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private void docToDB(String xmlPath,String project) throws Exception {
		
		Document doc = FileUploadUtil.getDoc(xmlPath);
		Element root = doc.getRootElement();
		String rootName = root.getName();
		if(!"permission".equals(rootName)) {
			throw new RuntimeException("xml根节点应该是permission，而不是"+rootName);
		}
		String curProject = root.attribute("project").getValue();
		if(root.attribute("project")==null) {
			throw new RuntimeException("xml配置的项目名称不能为空");
		}
		boolean flag = hasManageLimit(project,curProject);
		if(!flag) {
			throw new RuntimeException("xml配置的项目名称错误，应为"+project+"中的一个");
		}
		project = curProject;
		//备份权限数据
		List<Permission> list = bakOldAuthority(project);
		Iterator<Element> iterator = root.elementIterator();
		while (iterator.hasNext()) {
			Element nextElement = iterator.next();
			String name = nextElement.getName();
			if("navigation".equals(name)) {
				processNavigation(nextElement,project,list);
			}else if("unavigation".equals(name)) {
				processUnavigation(nextElement,project,list);
			}else if("exclude".equals(name)) {
				processExclude(nextElement,project);
			}else {
				throw new RuntimeException("permission元素只能有三种子元素：navigation、unavigation、exclude");
			}
		}
		//更新角色权限关联表
		updateRolePermission(list);
		//删除老权限
		deleteOldPermission(project+"_bak_");
		//清空权限映射表
		deletePermissionMapping();
		//创建或赋予一个拥有所有权限的角色和用户
		createGod(project);
		//保存最新配置文件
		saveConfigure(project,xmlPath);
	}
	
	private void saveConfigure(String project,String xmlPath) {
		List<String> parameters = new ArrayList<String>();
		parameters.add(project);
		List<PermissionConfigure> list = authorityDao.findByHql("from PermissionConfigure where project=?", parameters);
		if(list.size()==0) {
			PermissionConfigure pc = new PermissionConfigure();
			pc.setProject(project);
			pc.setPath(xmlPath);
			pc.setCreateTime(new Date());
			pc.setLastUpdateTime(new Date());
			authorityDao.save(pc);
		}else {
			PermissionConfigure pc = list.get(0);
			pc.setPath(xmlPath);
			authorityDao.update(pc);
		}
	}
	
	/**
	 * 创建或赋予一个拥有所有权限的角色和用户
	 * @throws Exception 
	 */
	private void createGod(String project) throws Exception {
		
		//1.创建或找到超级管理员角色
		Role roleOfGod = new Role();
		List<String> parameters = new ArrayList<String>();
		parameters.add(Constants.ROLEOFGOD);
		List<Role> list = authorityDao.findByHql("from Role where roleName=?", parameters);
		if(list!=null&&list.size()>0) {
			roleOfGod = list.get(0);
		}else {
			roleOfGod.setRoleName(Constants.ROLEOFGOD);
			roleOfGod.setMemo(Constants.ROLEOFGOD);
			roleOfGod.setCreateTime(new Date());
			roleOfGod.setLastUpdTime(new Date());
			authorityDao.save(roleOfGod);
		}
		String roleid = roleOfGod.getId();
		
		//2.删除超级管理员角色权限关系
		List<String> parameter = new ArrayList<String>();
		parameter.add(roleid);
		parameter.add(project);
		authorityDao.excuteHql("delete from RolePermission where roleId=? and project=?", parameter);
		
		//3.给超级管理员角色赋予该系统的所有权限
		List<String> parameters2 = new ArrayList<String>();
		parameters2.add(project);
		List<Permission> perlist =  authorityDao.findByHql("from Permission where projectName=?", parameters2);
		for(Permission per:perlist) {
			String permissionId = per.getId();
			RolePermission rp = new RolePermission();
			rp.setPermissionId(permissionId);
			rp.setRoleId(roleid);
			rp.setProject(project);
			authorityDao.save(rp);
		}
		
		//4.找到超级管理员用户,找不到就抛出异常
		List<String> parameters3 = new ArrayList<String>();
		parameters3.add(Constants.USEROFGOD);
		List<User> usrlist = authorityDao.findByHql("from User where username=?", parameters3);
		if(usrlist==null||usrlist.size()==0) {
			throw new Exception("超级管理员用户不存在！");
		}
		User userOfGod = usrlist.get(0);
		String userid = userOfGod.getId();
		
		//5.超级管理员用户赋予超级管理员角色
		List<String> parameters4 = new ArrayList<String>();
		parameters4.add(roleid);
		parameters4.add(userid);
		List<UserRole> userRoleList = authorityDao.findByHql("from UserRole where roleId=? and userId=?", parameters4);
		if(userRoleList==null||userRoleList.size()==0) {
			UserRole ur = new UserRole();
			ur.setUserId(userid);
			ur.setRoleId(roleid);
			authorityDao.save(ur);
		}
		
	}
	/**
	 * 验证该系统有没有权限导入该权限配置文件
	 * @param project
	 * @param curProject
	 * @return
	 */
	private boolean hasManageLimit(String project,String curProject) {
		boolean flag = false;
		String[] projects = project.split(",");
		if(projects!=null) {
			for(String pro :projects) {
				if(pro.equals(curProject)) {
					flag=true;
					break;
				}
			}
		}
		return flag;
	}
	/**
	 * 处理导航相关权限，便于动态生成导航目录。
	 */
	@SuppressWarnings({  "unchecked" })
	private void processNavigation(Element element,String project,List<Permission> list) {
		Iterator<Element> iterator = element.elementIterator();
		while (iterator.hasNext()) {
			Element nextElement = iterator.next();
			Permission permission = new Permission();
			permission.setProjectName(project);
			Attribute memo = nextElement.attribute("memo");
			if(memo==null||StringUtil.isEmpty(memo.getValue())) {
				throw new RuntimeException("memo属性不能为空");
			}
			permission.setMemo(memo.getValue());
			permission.setParentId(Constants.ROOT_PARENT);
			permission.setCreateTime(new Date());
			permission.setLastUpdTime(new Date());
			String nextElementName = nextElement.getName();
			if(nextElement.attribute("ishide")!=null&&nextElement.attribute("ishide").getValue()=="1"){
				permission.setIsHide("1");
			}else {
				permission.setIsHide("0");
			}
			String uiSref = "";
			if("catalog".equals(nextElementName)) {
				Attribute url = nextElement.attribute("url");
				if(url==null||StringUtil.isEmpty(url.getValue())) {
					throw new RuntimeException("catalog元素的url属性不能为空，作为唯一编号");
				}
				//catalog的url值必须保证唯一
				List<String> parameters = new ArrayList<String>();
				parameters.add(project);
				parameters.add(url.getValue());
				List<Permission> perList = authorityDao.findByHql("from Permission where projectName=? and url=?", parameters);
				if(perList!=null&&perList.size()>1) {
					throw new RuntimeException("catalog元素的url属性值必须唯一");
				}
				permission.setUrl(url.getValue());
				permission.setType(Constants.CATALOG_TYPE);
			}else if("page".equals(nextElementName)) {
				Attribute url = nextElement.attribute("url");
				if(url==null||StringUtil.isEmpty(url.getValue())) {
					throw new RuntimeException("page元素的url属性不能为空");
				}
				Attribute uiSrefArr = nextElement.attribute("ui-sref");
				if(uiSrefArr!=null) {
					uiSref = uiSrefArr.getValue();
				}
				permission.setUrl(url.getValue());
				permission.setType(Constants.PAGE_TYPE);
			}else {
				throw new RuntimeException("xml中navigation元素的子元素只允许是catalog和page");
			}
			authorityDao.save(permission);
			Catalog catalog = new Catalog();
			catalog.setPermissionId(permission.getId());
			catalog.setProject(project);
			catalog.setCreateTime(new Date());
			if(!StringUtil.isEmpty(uiSref)) {
				catalog.setUiSref(uiSref);
			}
			authorityDao.save(catalog);
			mapping(permission,list);
			if(nextElement.elements().size()>0) { 
				elementToDB(nextElement,permission,list);
			}
		}
	}
	
	/**
	 * 处理非导航相关权限。
	 */
	@SuppressWarnings("unchecked")
	private void processUnavigation(Element element,String project,List<Permission> list) {
		Iterator<Element> iterator = element.elementIterator();
		while (iterator.hasNext()) {
			Element nextElement = iterator.next();
			Permission permission = new Permission();
			permission.setProjectName(project);
			Attribute memo = nextElement.attribute("memo");
			if(memo==null||StringUtil.isEmpty(memo.getValue())) {
				throw new RuntimeException("memo属性不能为空");
			}
			permission.setMemo(memo.getValue());
			permission.setParentId(Constants.ROOT_PARENT);
			permission.setCreateTime(new Date());
			permission.setLastUpdTime(new Date());
			String nextElementName = nextElement.getName();
			if(nextElement.attribute("ishide")!=null&&nextElement.attribute("ishide").getValue()=="1"){
				permission.setIsHide("1");
			}else {
				permission.setIsHide("0");
			}
			if("button".equals(nextElementName)) {
				Attribute url = nextElement.attribute("url");
				if(url==null||StringUtil.isEmpty(url.getValue())) {
					throw new RuntimeException("button元素的url属性不能为空");
				}
				permission.setUrl(url.getValue());
				permission.setType(Constants.BUTTON_TYPE);
			}else if("page".equals(nextElementName)) {
				Attribute url = nextElement.attribute("url");
				if(url==null||StringUtil.isEmpty(url.getValue())) {
					throw new RuntimeException("page元素的url属性不能为空");
				}
				permission.setUrl(url.getValue());
				permission.setType(Constants.PAGE_TYPE);
			}else {
				throw new RuntimeException("xml中unavigation元素的子元素只允许是button和page");
			}
			authorityDao.save(permission);
			mapping(permission,list);
			if(nextElement.elements().size()>0) { 
				elementToDB(nextElement,permission,list);
			}
		}
	}
	/**
	 * 处理不需权限过滤的url。可以节省写过滤器的成本
	 */
	@SuppressWarnings("unchecked")
	private void processExclude(Element element,String project) {
		Iterator<Element> iterator = element.elementIterator();
		while(iterator.hasNext()) {
			Element nextElement = iterator.next();
			PermissionExclude pe = new PermissionExclude();
			if(!"url".equals(nextElement.getName())){
				throw new RuntimeException("xml中exclude元素的子元素只允许是url");
			}
			Attribute url = nextElement.attribute("url");
			if(url==null||StringUtil.isEmpty(url.getValue())) {
				throw new RuntimeException("url元素的url属性不能为空");
			}
			pe.setUrl(url.getValue());
			pe.setProject(project);
			pe.setCreateTime(new Date());
			authorityDao.save(pe);
		}
	}
	
	private void updateRolePermission(List<Permission> list) {
		Set<String> set = new HashSet<String>();
		for(Permission per:list) {
			set.add(per.getId());
		}
		List<PermissionMapping> pmList = authorityDao.findByHql("from PermissionMapping", null);
		for(PermissionMapping pm:pmList) {
			String newId = pm.getNewPermission();
			String oldId = pm.getOldPermission();
			List<String> parameter = new ArrayList<String>();
			parameter.add(oldId);
			List<RolePermission> rpList = authorityDao.findByHql("from RolePermission where permissionId=?", parameter);
			for(RolePermission rp:rpList) {
				rp.setPermissionId(newId);
				authorityDao.update(rp);
			}
			set.remove(oldId);
		}
		if(set!=null&&set.size()>0) {
			for(String perid:set) {
				List<String> parameter = new ArrayList<String>();
				parameter.add(perid);
				authorityDao.excuteHql("delete from RolePermission where permissionId=?", parameter);
			}
		}
	}
	
	private void deleteOldPermission(String projectBak) {
		List<String> parameter = new ArrayList<String>();
		parameter.add(projectBak);
		authorityDao.excuteHql("delete from Permission where projectName=?", parameter);
		authorityDao.excuteHql("delete from PermissionExclude where project=?", parameter);
		authorityDao.excuteHql("delete from Catalog where project=?", parameter);
	}
	
	private void deletePermissionMapping() {
		authorityDao.excuteHql("delete from PermissionMapping", null);
	}
	
	private List<Permission> bakOldAuthority(String project) {
		List<String> parameter = new ArrayList<String>();
		parameter.add(project);
		List<Permission> list = authorityDao.findByHql("from Permission where projectName=?", parameter);
		for(Permission per:list) {
			per.setProjectName(project+"_bak_");
			authorityDao.update(per);
		}
		List<PermissionExclude> list1 = authorityDao.findByHql("from PermissionExclude where project=?", parameter);
		for(PermissionExclude pe:list1) {
			pe.setProject(project+"_bak_");
			authorityDao.update(pe);
		}
		List<Catalog> list2 = authorityDao.findByHql("from Catalog where project=?", parameter);
		for(Catalog ca:list2) {
			ca.setProject(project+"_bak_");
			authorityDao.update(ca);
		}
		return list;
	}
	/**
	 * @author XYL
	 * 把每一个元素保存到数据库
	 * @param nextElement
	 * @param permission
	 * @param list
	 */
	@SuppressWarnings({ "unchecked" })
	private void elementToDB(Element nextElement,Permission permission,List<Permission> list) {
		String parentId = permission.getId();
		String type = permission.getType();
		String projectName = permission.getProjectName();
		Iterator<Element> iterator = nextElement.elementIterator();
		String elementName = nextElement.getName();
		while(iterator.hasNext()) {
			Element nextElement1 = iterator.next();
			Permission permission1 = new Permission();
			permission1.setParentId(parentId);
			permission1.setProjectName(projectName);
			Attribute memo = nextElement1.attribute("memo");
			if(memo==null||StringUtil.isEmpty(memo.getValue())) {
				throw new RuntimeException("memo属性不能为空");
			}
			permission1.setMemo(memo.getValue());
			permission1.setCreateTime(new Date());
			permission1.setLastUpdTime(new Date());
			if(nextElement1.attribute("ishide")!=null&&nextElement1.attribute("ishide").getValue()=="1"){
				permission1.setIsHide("1");
			}else {
				permission1.setIsHide("0");
			}
			String nextElement1Name = nextElement1.getName();
			String uiSref = "";
			if(Constants.CATALOG_TYPE.equals(type)) {
				if("catalog".equals(nextElement1Name)) {
					Attribute url = nextElement1.attribute("url");
					if(url==null||StringUtil.isEmpty(url.getValue())) {
						throw new RuntimeException("catalog元素的url属性不能为空，作为编号");
					}
					//catalog的url值必须保证唯一
					List<String> parameters = new ArrayList<String>();
					parameters.add(projectName);
					parameters.add(url.getValue());
					List<Permission> perList = authorityDao.findByHql("from Permission where projectName=? and url=?", parameters);
					if(perList!=null&&perList.size()>1) {
						throw new RuntimeException("catalog元素的url属性值必须唯一");
					}
					permission1.setUrl(url.getValue());
					permission1.setType(Constants.CATALOG_TYPE);
				}else if("page".equals(nextElement1Name)) {
					Attribute url = nextElement1.attribute("url");
					if(url==null||StringUtil.isEmpty(url.getValue())) {
						throw new RuntimeException("page元素的url属性不能为空");
					}
					Attribute uiSrefArr = nextElement1.attribute("ui-sref");
					if(uiSrefArr!=null) {
						uiSref = uiSrefArr.getValue();
					}
					permission1.setUrl(url.getValue());
					permission1.setType(Constants.PAGE_TYPE);
				}else {
					throw new RuntimeException("xml中catalog元素的子元素只允许是catalog和page");
				}
			}else if(Constants.PAGE_TYPE.equals(type)) {
				if("button".equals(nextElement1Name)) {
					Attribute url = nextElement1.attribute("url");
					if(url==null||StringUtil.isEmpty(url.getValue())) {
						throw new RuntimeException("button元素的url属性不能为空");
					}
					permission1.setUrl(url.getValue());
					permission1.setType(Constants.BUTTON_TYPE);
				}else if("page".equals(nextElement1Name)) {
					Attribute url = nextElement1.attribute("url");
					if(url==null||StringUtil.isEmpty(url.getValue())) {
						throw new RuntimeException("page元素的url属性不能为空");
					}
					permission1.setUrl(url.getValue());
					permission1.setType(Constants.PAGE_TYPE);
				}else {
					throw new RuntimeException("xml中page元素的子元素只允许是button和page");
				}
			}else if(Constants.BUTTON_TYPE.equals(type)) {
				throw new RuntimeException("xml中button元素不允许有子元素");
			}
			authorityDao.save(permission1);
			//当父元素是catalog，就存到t_catalog表
			if("catalog".equals(elementName)) {
				Catalog catalog = new Catalog();
				catalog.setPermissionId(permission1.getId());
				catalog.setProject(projectName);
				catalog.setCreateTime(new Date());
				if(!StringUtil.isEmpty(uiSref)) {
					catalog.setUiSref(uiSref);
				}
				authorityDao.save(catalog);
			}
			mapping(permission1,list);
			if(nextElement1.elements().size()>0) { 
				elementToDB(nextElement1,permission1,list);
			}
		}
	}
	/**
	 * 批量导入时，建立新老权限id映射
	 * @param permission
	 * @param list
	 */
	private void mapping(Permission permission,List<Permission> list) {
		String url = permission.getUrl();
		String newId = permission.getId();
		String newParentId = permission.getParentId();
		String oldId = null;
		String oldParentId = null;
		if(Constants.CATALOG_TYPE.equals(permission.getType())){
			for(Permission per:list) {
				if(url!=null&&url.equals(per.getUrl())) {
					oldId = per.getId();
					break;
				}
			}
		} else if(Constants.PAGE_TYPE.equals(permission.getType())) {
			
			Permission parentPermission  = authorityDao.findById(newParentId, Permission.class);
			if(Constants.ROOT_PARENT.equals(newParentId)) {//如果没有上级，就以本身url决定原来的权限id
				for(Permission per:list) {
					if(url!=null&&url.equals(per.getUrl())) {
						oldId = per.getId();
						break;
					}
				}
			}else if(parentPermission==null) {
				return;
			}else {
				String parentType = parentPermission.getType();
				if(Constants.CATALOG_TYPE.equals(parentType)) {//如果上级是目录，就以自身url决定原来的权限id
					for(Permission per:list) {
						if(url!=null&&url.equals(per.getUrl())) {
							oldId = per.getId();
							break;
						}
					}
				} else if(Constants.PAGE_TYPE.equals(parentType)) {//如果上级是页面，就以自身url和父url决定原来的权限id
					String parentUrl = parentPermission.getUrl();
					for(Permission per:list) {
						if(url!=null&&url.equals(per.getUrl())) {
							oldParentId = per.getParentId();
							String oldParentUrl = null;
							for(Permission perm:list) {
								if(oldParentId.equals(perm.getId())) {
									oldParentUrl = perm.getUrl();
									break;
								}
							}
							if(parentUrl!=null&&parentUrl.equals(oldParentUrl)) {
								oldId = per.getId();
								break;
							}
						}
					}
				}
			}
			
		} else if(Constants.BUTTON_TYPE.equals(permission.getType())) {//如果是button，由本身的url和父url决定原来的权限id
			
			Permission parentPermission  = authorityDao.findById(newParentId, Permission.class);
			if(Constants.ROOT_PARENT.equals(newParentId)) {//如果没有上级，就以本身url决定原来的权限id
				for(Permission per:list) {
					if(url!=null&&url.equals(per.getUrl())) {
						oldId = per.getId();
						break;
					}
				}
			}else if(parentPermission==null) {
				return;
			}else {
				String parentUrl = parentPermission.getUrl();
				for(Permission per:list) {
					if(url!=null&&url.equals(per.getUrl())) {
						oldParentId = per.getParentId();
						String oldParentUrl = null;
						for(Permission perm:list) {
							if(oldParentId.equals(perm.getId())) {
								oldParentUrl = perm.getUrl();
								break;
							}
						}
						if(parentUrl!=null&&parentUrl.equals(oldParentUrl)) {
							oldId = per.getId();
							break;
						}
					}
				}
			}
			
		} else {
			return;
		}
		
		if(StringUtil.isNotEmpty(oldId)) {
			PermissionMapping pm = new PermissionMapping();
			pm.setNewPermission(newId);
			pm.setOldPermission(oldId);
			authorityDao.save(pm);
		}
	}
	
	/**
	 * 下载权限配置文件模板
	 */
	@Override
	public Map<String, String> downloadPermissionTemplate() {
		Map<String,String> map = new HashMap<String,String>();
		try {
			String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath()+Constants.PERMISSION_TEMPLATE;
			String name = Constants.PERMISSION_TEMPLATE_NAME;
			String data = FileByteBuffUtil.getFileContents(path);
			map.put(Constants.RETURN_DATA, data);
			map.put("name", name);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	
	/**
	 * 下载某系统的最新配置文件
	 */
	@Override
	public Map<String, String> downloadNewestPermission(String projectname) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			List<String> parameters = new ArrayList<String>();
			parameters.add(projectname);
			List<PermissionConfigure> list = authorityDao.findByHql("from PermissionConfigure where project=?", parameters);
			if(list.size()==0) {
				map.put(Constants.RETURN_CODE,"-1");
				return map;
			}
			String path = list.get(0).getPath();
			String data = FileByteBuffUtil.getFileContents(path);
			String name = projectname+Constants.NEWEST_PERMISSION ;
			map.put(Constants.RETURN_DATA, data);
			map.put("name", name);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}

	@Override
	public Map<String, String> addRole(String userid,String rolename, String memo) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			Role role = new Role();
			List<String> parameters = new ArrayList<String>();
			parameters.add(rolename);
			List<Role> list = authorityDao.findByHql("from Role where roleName=?", parameters);
			if(list!=null&&list.size()>0) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			role.setRoleName(rolename);
			role.setMemo(memo);
			role.setCreateTime(new Date());
			role.setLastUpdTime(new Date());
			authorityDao.save(role);
			UserRole ur = new UserRole();
			ur.setRoleId(role.getId());
			ur.setUserId(userid);
			authorityDao.save(ur);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	/**
	 * @author XYL
	 * 删除某角色
	 */
	@Override
	public Map<String, String> delRole(String roleid) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			//1.删除用户角色关系
			deleteUserRole(roleid);
			//2.删除角色权限关系
			deleteRolePermission(roleid);
			//3.删除角色本身
			deleteRole(roleid);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	
	private void deleteUserRole(String roleid) {
		List<String> parameter = new ArrayList<String>();
		parameter.add(roleid);
		authorityDao.excuteHql("delete from UserRole where roleId=?", parameter);
	}
	
	private void deleteRolePermission(String roleid) {
		List<String> parameter = new ArrayList<String>();
		parameter.add(roleid);
		authorityDao.excuteHql("delete from RolePermission where roleId=?", parameter);
	}
	
	private void deleteRole(String roleid) {
		List<String> parameter = new ArrayList<String>();
		parameter.add(roleid);
		authorityDao.excuteHql("delete from Role where id=?", parameter);
	}
	

	@Override
	public Map<String, String> updRole(String roleid,String rolename, String memo) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			Role role = authorityDao.findById(roleid, Role.class);
			if(role==null) {
				map.put(Constants.RETURN_CODE, "-1");//该角色不存在
				return map;
			}
			if(!StringUtil.isEmpty(rolename)) {
				List<String> parameters = new ArrayList<String>();
				parameters.add(rolename);
				List<Role> list = authorityDao.findByHql("from Role where roleName=?", parameters);
				list.remove(role);
				if(list.size()>0) {
					map.put(Constants.RETURN_CODE, "-2");//角色名称已存在
					return map;
				}
				role.setRoleName(rolename);
			}
			if(!StringUtil.isEmpty(memo)) {
				role.setMemo(memo);
			}
			authorityDao.update(role);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	/**
	 * @author XYL
	 * 获取某个角色的详情信息
	 */
	@Override
	public Map<String, String> getRole(String roleid) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			Role role = authorityDao.findById(roleid, Role.class);
			String data = JsonUtil.getJsonStrByConfigFromObj(role);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put("data", data);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	
	/**
	 * @author XYL
	 * 获取某些系统的全部权限
	 */
	@Override
	public Map<String, String> queProAllPermission(List<String> projectnames) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			List<Object> list = new ArrayList<Object>();
			for(String project:projectnames) {
				Permission per = getPermissionTree(project);
				list.add(per);
			}
			String data = JsonUtil.getJsonStrFromList(list);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put("data", data);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	
	private Permission getPermissionTree(String project) {
		Permission rootPer = new Permission();
		rootPer.setMemo(project);
		List<String> parameters = new ArrayList<String>();
		parameters.add(Constants.ROOT_PARENT);
		parameters.add(project);
		List<Permission> list = authorityDao.findByHql("from Permission where parentId=? and projectName=?", parameters);
		if(list!=null&&list.size()>0) {
			rootPer.setZlist(list);
			recursiveQuery(list,project);
		}
		return rootPer;
	}
	
	private void recursiveQuery(List<Permission> list,String project) {
		for(Permission per:list) {
			List<String> parameters = new ArrayList<String>();
			parameters.add(per.getId());
			parameters.add(project);
			List<Permission> list1 = authorityDao.findByHql("from Permission where parentId=? and projectName=?", parameters);
			if(list1.size()>0) {
				per.setZlist(list1);
				recursiveQuery(list1,project);
			}
		}
	}

	/**
	 * @author XYL
	 * 获取某用户在某些系统上的所有权限
	 */
	@Override
	public Map<String, String> queProUserPermission(String userid,List<String> projectnames){
		Map<String,String> map = new HashMap<String,String>();
		
		try {
			if(StringUtil.isEmpty(userid)) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			List<Object> list = new ArrayList<Object>();
			for(String project:projectnames) {
				Permission per = getUserPermissionTree(project,userid);
				list.add(per);
			}
			String data = JsonUtil.getJsonStrFromList(list);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put("data", data);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		
		return map;
	}
	
	private Permission getUserPermissionTree(String project,String userid) {
		Permission rootPer = new Permission();
		rootPer.setMemo(project);
		List<String> parameters = new ArrayList<String>();
		parameters.add(Constants.ROOT_PARENT);
		parameters.add(project);
		List<Permission> list = authorityDao.findByHql("from Permission where parentId=? and projectName=?", parameters);
		List<Permission> listTemp = new ArrayList<Permission>();
		Set<String> set = getPermissionIdSet(userid,project);
		if(list!=null&&list.size()>0) {
			for(Permission per:list) {
				String id = per.getId();
				if(!set.contains(id)){
					listTemp.add(per);
				}
			}
			list.removeAll(listTemp);
		}
		if(list!=null&&list.size()>0) {
			rootPer.setZlist(list);
			recursiveUserQuery(list,project,userid,set);
		}
		return rootPer;
	}
	
	private void recursiveUserQuery(List<Permission> list,String project,String userid,Set<String> set) {
		for(Permission per:list) {
			List<String> parameters = new ArrayList<String>();
			parameters.add(per.getId());
			parameters.add(project);
			List<Permission> list1 = authorityDao.findByHql("from Permission where parentId=? and projectName=?", parameters);
			List<Permission> listTemp = new ArrayList<Permission>();
			if(list1!=null&&list1.size()>0) {
				for(Permission per1:list1) {
					if(Constants.CATALOG_TYPE.equals(per.getType())&&Constants.PAGE_TYPE.equals(per1.getType())) {
						List<String> cp = new ArrayList<String>();
						cp.add(per1.getId());
						List<Catalog> clist = authorityDao.findByHql("from Catalog where permissionId=?", cp);
						if(clist.size()>0&&StringUtil.isNotEmpty(clist.get(0).getUiSref())) {
							per1.setUiSref(clist.get(0).getUiSref());
						}
					}
					String id = per1.getId();
					if(!set.contains(id)){
						listTemp.add(per1);
					}
				}
				list1.removeAll(listTemp);
			}
			if(list1!=null&&list1.size()>0) {
				per.setZlist(list1);
				recursiveUserQuery(list1,project,userid,set);
			}
		}
	}
	
	private Set<String> getPermissionIdSet(String userid,String project) {
		Set<String> set = new HashSet<String>();
		Set<String> roleSet = getRoleSet(userid);
		for(String roleid:roleSet) {
			set.addAll(getPermissionIdSetByRole(roleid,project));
		}
		return set;
	}
	
	private Set<String> getPermissionIdSetByRole(String roleid,String project) {
		Set<String> permissionSet = new HashSet<String>();
		List<String> parameters = new ArrayList<String>();
		parameters.add(roleid);
		parameters.add(project);
		List<RolePermission> list = authorityDao.findByHql("from RolePermission where roleId=? and project=?", parameters);
		if(list!=null&&list.size()>0) {
			for(RolePermission rp:list) {
				permissionSet.add(rp.getPermissionId());
			}
		}
		return permissionSet;
	}
	
	private Set<String> getRoleSet(String userid) {
		Set<String> roleSet = new HashSet<String>();
		List<String> parameters = new ArrayList<String>();
		parameters.add(userid);
		List<UserRole> list = authorityDao.findByHql("from UserRole where userId=?", parameters);
		for(UserRole userRole:list) {
			roleSet.add(userRole.getRoleId());
		}
		return roleSet;
	} 
	
	/**
	 * @author XYL
	 * 获取导航目录结构
	 */
	@Override
	public Map<String, String> getCatalog(String userid, String projectname)  {
		Map<String,String> map = new HashMap<String,String>();
		
		try {
			if(StringUtil.isEmpty(userid)) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			List<Object> list = new ArrayList<Object>();
			Permission per = getCatalogTree(projectname,userid);
			list.add(per);
			String data = JsonUtil.getJsonStrFromList(list);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put("data", data);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		
		return map;
	}
	
	private Permission getCatalogTree(String projectname,String userid) {
		Permission rootPer = new Permission();
		rootPer.setMemo(projectname);
		List<String> parameters = new ArrayList<String>();
		parameters.add(Constants.ROOT_PARENT);
		parameters.add(projectname);
		List<Permission> list = authorityDao.findByHql("from Permission where parentId=? and projectName=?", parameters);
		List<Permission> listTemp = new ArrayList<Permission>();
		Set<String> set = getCatalogSet(userid,projectname);
		if(list!=null&&list.size()>0) {
			for(Permission per:list) {
				String id = per.getId();
				//如果是页面类型，就要检查catalog表里面有没有前端映射
				if(Constants.PAGE_TYPE.equals(per.getType())) {
					List<String> cp = new ArrayList<String>();
					cp.add(id);
					List<Catalog> clist = authorityDao.findByHql("from Catalog where permissionId=?", cp);
					if(clist.size()>0&&StringUtil.isNotEmpty(clist.get(0).getUiSref())) {
						per.setUiSref(clist.get(0).getUiSref());
					}
				}
				if(!set.contains(id)){
					listTemp.add(per);
				}
			}
			list.removeAll(listTemp);
		}
		if(list!=null&&list.size()>0) {
			rootPer.setZlist(list);
			recursiveUserQuery(list,projectname,userid,set);
		}
		return rootPer;
	}
	
	private Set<String> getCatalogSet(String userid,String projectname) {
		Set<String> result = new HashSet<String>();
		Set<String> set = new HashSet<String>();
		Set<String> roleSet = getRoleSet(userid);
		for(String roleid:roleSet) {
			set.addAll(getPermissionIdSetByRole(roleid,projectname));
		}
		List<String> parameters = new ArrayList<String>();
		parameters.add(projectname);
		List<Catalog> list = authorityDao.findByHql("from Catalog where project=?", parameters);
		Set<String> set1 = new HashSet<String>();
		if(list!=null&&list.size()>0) {
			for(Catalog c:list) {
				set1.add(c.getPermissionId());
			}
		}
		result.addAll(set);
		result.retainAll(set1);
		return result;
	}
	
	/**
	 * @author XYL
	 * 
	 * 获取某角色在某些系统的权限
	 */
	@Override
	public Map<String, String> queProRolePermission(String roleid,List<String> projectnames) {
		Map<String,String> map = new HashMap<String,String>();
		
		try {
			List<Object> list = new ArrayList<Object>();
			for(String project:projectnames) {
				Permission per = getRolePermissionTree(project,roleid);
				list.add(per);
			}
			String data = JsonUtil.getJsonStrFromList(list);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put("data", data);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		
		return map;
	}
	
	private Permission getRolePermissionTree(String project,String roleid) {
		Permission rootPer = new Permission();
		rootPer.setMemo(project);
		List<String> parameters = new ArrayList<String>();
		parameters.add(Constants.ROOT_PARENT);
		parameters.add(project);
		List<Permission> list = authorityDao.findByHql("from Permission where parentId=? and projectName=?", parameters);
		List<Permission> listTemp = new ArrayList<Permission>();
		Set<String> set = getPermissionIdSetByRole(roleid,project);
		if(list!=null&&list.size()>0) {
			for(Permission per:list) {
				String id = per.getId();
				if(!set.contains(id)){
					listTemp.add(per);
				}
			}
			list.removeAll(listTemp);
		}
		if(list!=null&&list.size()>0) {
			rootPer.setZlist(list);
			recursiveRoleQuery(list,project,roleid,set);
		}
		return rootPer;
	}
	
	private void recursiveRoleQuery(List<Permission> list,String project,String roleid,Set<String> set) {
		for(Permission per:list) {
			List<String> parameters = new ArrayList<String>();
			parameters.add(per.getId());
			parameters.add(project);
			List<Permission> list1 = authorityDao.findByHql("from Permission where parentId=? and projectName=?", null);
			List<Permission> listTemp = new ArrayList<Permission>();
			if(list1!=null&&list1.size()>0) {
				for(Permission per1:list1) {
					String id = per1.getId();
					if(!set.contains(id)){
						listTemp.add(per1);
					}
				}
				list1.removeAll(listTemp);
			}
			if(list1!=null&&list1.size()>0) {
				per.setZlist(list1);
				recursiveRoleQuery(list1,project,roleid,set);
			}
		}
	}
	
	@Override
	public Map<String, String> getUserRolePermission(String userid,String roleid, List<String> projectnames) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			List<Object> list = new ArrayList<Object>();
			for(String project:projectnames) {
				Permission per = getUserRolePermissionTree(userid,roleid,project);
				list.add(per);
			}
			String data = JsonUtil.getJsonStrFromList(list);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put("data", data);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	
	private Permission getUserRolePermissionTree(String userid,String roleid,String project) {
		Permission rootPer = new Permission();
		rootPer.setMemo(project);
		List<String> parameters = new ArrayList<String>();
		parameters.add(Constants.ROOT_PARENT);
		parameters.add(project);
		List<Permission> list = authorityDao.findByHql("from Permission where parentId=? and projectName=?", parameters);
		List<Permission> listTemp = new ArrayList<Permission>();
		Set<String> userPermissionSet = this.getPermissionIdSet(userid, project);
		Set<String> rolePermissionSet = getPermissionIdSetByRole(roleid,project);
		if(list!=null&&list.size()>0) {
			for(Permission per:list) {
				String id = per.getId();
				if(!userPermissionSet.contains(id)){
					listTemp.add(per);
				}
				if(rolePermissionSet.contains(id)) {
					per.setCheckbox(Constants.ROLE_CHECKED);
					rootPer.setCheckbox(Constants.ROLE_CHECKED);
				}else{
					per.setCheckbox(Constants.ROLE_UNCHECKED);
				}
			}
			list.removeAll(listTemp);
		}
		if(list!=null&&list.size()>0) {
			rootPer.setZlist(list);
			recursiveUserRoleQuery(list,project,userid,userPermissionSet,roleid,rolePermissionSet);
		}
		return rootPer;
	}
	
	private void recursiveUserRoleQuery(List<Permission> list,String project,String userid,Set<String> userPermissionSet,String roleid,Set<String> rolePermissionSet) {
		for(Permission per:list) {
			List<String> parameters = new ArrayList<String>();
			parameters.add(per.getId());
			parameters.add(project);
			List<Permission> list1 = authorityDao.findByHql("from Permission where parentId=? and projectName=?", parameters);
			List<Permission> listTemp = new ArrayList<Permission>();
			if(list1!=null&&list1.size()>0) {
				for(Permission per1:list1) {
					String id = per1.getId();
					if(!userPermissionSet.contains(id)){
						listTemp.add(per1);
					}
					if(rolePermissionSet.contains(id)) {
						per1.setCheckbox(Constants.ROLE_CHECKED);
					}else{
						per1.setCheckbox(Constants.ROLE_UNCHECKED);
					}
				}
				list1.removeAll(listTemp);
			}
			if(list1!=null&&list1.size()>0) {
				per.setZlist(list1);
				recursiveUserRoleQuery(list1,project,userid,userPermissionSet,roleid,rolePermissionSet);
			}
		}
	}
	
	
	/***
	 * @author XYL
	 * 设置某些系统的角色权限关系
	 */
	@Override
	public Map<String, String> setRolePermission(String roleid,List<String> permissionids,List<String> projects) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			Role role = authorityDao.findById(roleid, Role.class);
			if(role==null) {
				map.put(Constants.RETURN_CODE, "-2");//角色不存在
				return map;
			}
			for(String project:projects) {
				List<String> parameter = new ArrayList<String>();
				parameter.add(roleid);
				parameter.add(project);
				authorityDao.excuteHql("delete from RolePermission where roleId=? and project=?", parameter);
			}
			for(String permissionid:permissionids) {
				Permission per = authorityDao.findById(permissionid, Permission.class);
				if(per!=null) {
					String project = per.getProjectName();
					if(!projects.contains(project)) {
						map.put(Constants.RETURN_CODE, "-1");//该权限所属项目不属于管理范围
						return map;
					}
					String parentId = per.getParentId();
					boolean b = permissionids.contains(parentId);
					if(Constants.ROOT_PARENT.equals(parentId)||b) {
						RolePermission rp = new RolePermission();
						rp.setPermissionId(permissionid);
						rp.setRoleId(roleid);
						rp.setProject(project);
						authorityDao.save(rp);
					}
				}else {
					map.put(Constants.RETURN_CODE, "-2");//如果权限不存在，说明可能已经被人修改。
					return map;
				}
			}
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		
		return map;
	}
	/**
	 * @author XYL
	 * 删除账号
	 */
	@Override
	public Map<String, String> delUser(String userid) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			List<String> parameter = new ArrayList<String>();
			parameter.add(userid);
			authorityDao.excuteHql("delete from UserRole where userId=?", parameter);
			authorityDao.excuteHql("delete from User where id=?", parameter);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	/**
	 * @author XYL
	 * 开启账号
	 */
	@Override
	public Map<String, String> openUser(String userid) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			User user = authorityDao.findById(userid,User.class);
			user.setOpenClose(1);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	/**
	 * @author XYL
	 * 关闭账号
	 */
	@Override
	public Map<String, String> closeUser(String userid) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			User user = authorityDao.findById(userid,User.class);
			user.setOpenClose(0);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	/**
	 * @author XYL
	 * 查询所有角色
	 */
	@Override
	public Map<String, String> queRole(Map<String,String> queryMap) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			String name = "";
			if(queryMap!=null&&queryMap.size()>0) {
				name = queryMap.get("name");
			}
			DetachedCriteria criteria = DetachedCriteria.forClass(Role.class);
			if(StringUtil.isNotEmpty(name)) {
				criteria.add(Restrictions.like("username", "%"+name+"%"));
			}
			List<Object>  list = authorityDao.findByDetachedCriteria(criteria);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put(Constants.RETURN_DATA, JsonUtil.getJsonStrFromList(list));
		} catch (Exception e) {
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		
		return map;
	}
	/**
	 * @author XYL
	 * 查询某用户在所有系统的所有角色
	 */
	@Override
	public Map<String, String> queRoleByUser(String userid,Map<String,String> queryMap) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			if(StringUtil.isEmpty(userid)) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			String name = "";
			if(queryMap!=null&&queryMap.size()>0) {
				name = queryMap.get("name");
			}
			List<String> parameters = new ArrayList<String>();
			parameters.add(userid);
			List<UserRole> list = authorityDao.findByHql("from UserRole where userId=?", parameters);
			List<Object> roleList = new ArrayList<Object>();
			for(UserRole ur:list) {
				Role r = ur.getRole();
				if(StringUtil.isNotEmpty(name)&&r.getRoleName().contains(name)) {
					roleList.add(r);
				}else if(StringUtil.isEmpty(name)) {
					roleList.add(r);
				}
			}
			String data = JsonUtil.getJsonStrFromList(roleList);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put("data", data);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		
		return map;
	}
	
	@Override
	public Map<String, String> getCuserUserRole(String curuserid,String userid, Map<String, String> queryMap) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			if(StringUtil.isEmpty(curuserid)) {
				map.put(Constants.RETURN_CODE, "-1");//当前用户id为空
				return map;
			}
			if(StringUtil.isEmpty(userid)) {
				map.put(Constants.RETURN_CODE, "-2");//用户id为空
				return map;
			}
			String name = "";
			if(queryMap!=null) {
				name = queryMap.get("name");
			}
			
			List<String> curParameters = new ArrayList<String>();
			curParameters.add(curuserid);
			List<UserRole> curList = authorityDao.findByHql("from UserRole where userId=?", curParameters);
			
			List<String> parameters = new ArrayList<String>();
			parameters.add(userid);
			List<UserRole> list = authorityDao.findByHql("from UserRole where userId=?", parameters);
			Set<String> set = new HashSet<String>();
			for(UserRole ur:list) {
				Role r = ur.getRole();
				set.add(r.getId());
			}
			
			List<Object> roleList = new ArrayList<Object>();
			for(UserRole ur:curList) {
				Role r = ur.getRole();
				String id = r.getId();
				if(set!=null) {
					if(set.contains(id)) {
						r.setCheckbox(Constants.ROLE_CHECKED);
					}else {
						r.setCheckbox(Constants.ROLE_UNCHECKED);
					}
				}
				if((StringUtil.isNotEmpty(name)&&r.getRoleName().contains(name))||StringUtil.isEmpty(name)) {
					roleList.add(r);
				}
			}
			
			String data = JsonUtil.getJsonStrFromList(roleList);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put("data", data);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	
	/**
	 * @author XYL
	 * 设置用户角色关系
	 */
	@Override
	public Map<String, String> setUserRole(String userid, List<String> roleids) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			User user = authorityDao.findById(userid, User.class);
			if(user==null) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			List<String> parameter = new ArrayList<String>();
			parameter.add(userid);
			authorityDao.excuteHql("delete from UserRole where userId=?", parameter);
			for(String roleid:roleids) {
				Role role = authorityDao.findById(roleid, Role.class);
				if(role!=null) {
					UserRole ur = new UserRole();
					ur.setRoleId(roleid);
					ur.setUserId(userid);
					authorityDao.save(ur);
				}
			}
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	/**
	 * @author XYL
	 * 权限过滤
	 */
	@Override
	public Map<String, String> authorityFilter(String userid, String url,String project) {
		Map<String,String> map = new HashMap<String,String>();
		
		try {
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(url);
			List<PermissionExclude> peList = authorityDao.findByHql("from PermissionExclude where url=?", parameters);
			if(peList!=null&&peList.size()>0) {
				map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
				map.put("flag", "true");
				return map;
			}
			
			if(StringUtil.isEmpty(userid)) {
				map.put(Constants.RETURN_CODE, "-1");
				map.put("flag", "false");
				return map;
			}
			parameters.add(project);
			List<Permission> list = authorityDao.findByHql("from Permission where url=? and projectName=?", parameters);
			boolean flag = false;
			for(Permission per:list) {
				String id = per.getId();
				String parentId = per.getParentId();
				Set<String> set = new HashSet<String>();
				set = getPermissionIdSet(userid, project);
				boolean flag1 = set.contains(id);
				if(Constants.ROOT_PARENT.equals(parentId)&&flag1) {
					flag = true;
					break;
				}else if(flag1) {
					boolean flag2 = set.contains(parentId);
					if(flag2) {
						flag = true;
						break;
					}
				}
			}
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put("flag", String.valueOf(flag));
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}
	
	@Override
	public Map<String, String> queUser(Map<String, String> queryMap) {
		Map<String,String> map = new HashMap<String,String>();
		try {
			String name = "";
			if(queryMap!=null&&queryMap.size()>0) {
				name = queryMap.get("name");
			}
			DetachedCriteria criteria = DetachedCriteria.forClass(User.class);
			if(StringUtil.isNotEmpty(name)) {
				criteria.add(Restrictions.like("username", "%"+name+"%"));
			}
			List<Object>  list = authorityDao.findByDetachedCriteria(criteria);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put(Constants.RETURN_DATA, JsonUtil.getJsonStrFromList(list));
		} catch (Exception e) {
			log.error(e.getMessage());
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
			return map;
		}
		return map;
	}

	@Override
	public void ping() {
		
	}


}
