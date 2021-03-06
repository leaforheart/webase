namespace java com.inveno.cps.authority.thrift

service AuthorityService {
	//上传权限配置文件
	map<string,string> uploadPermission(1:binary file,2:string filename),
	//一键导入某系统的所有权限
	map<string,string> importPermission(1:string projectname,2:string xmlpath),
	
	//下载权限配置文件模板
	map<string,string> downloadPermissionTemplate(),
	
	//下载某系统的最新权限配置文件
	map<string,string> downloadNewestPermission(1:string projectname),
	
	//增加角色
	map<string,string> addRole(1:string userid;2:string rolename,3:string memo),
	//删除角色
	map<string,string> delRole(1:string roleid),
	//修改角色
	map<string,string> updRole(1:string id,2:string rolename,3:string memo),
	//获取角色详情
	map<string,string> getRole(1:string roleid),
	//查看某些系统的所有权限
	map<string,string> queProAllPermission(1:list<string> projectnames),
	//查看当前用户的某些系统的所有权限
	map<string,string> queProUserPermission(1:string userid,2:list<string> projectnames),
	//获取导航目录结构
	map<string,string> getCatalog(1:string userid,2:string projectname),
	//获取当前角色拥有的某些系统的权限
	map<string,string> queProRolePermission(1:string roleid,2:list<string> projectnames),
	//首先获取当前用户在某些系统的所有权限，然后标记某角色拥有的权限。
	map<string,string> getUserRolePermission(1:string userid,2:string roleid,3:list<string> projectnames),
	//设置某些系统的角色权限关系
	map<string,string> setRolePermission(1:string roleid,2:list<string> permissionids,3:list<string> projectnames),
	//删除用户
	map<string,string> delUser(1:string userid),
	//开启用户
	map<string,string> openUser(1:string userid),
	//关闭用户
	map<string,string> closeUser(1:string userid),
	//查看全部角色
	map<string,string> queRole(1:map<string,string> queryMap),
	//查看当前用户所拥有的角色
	map<string,string> queRoleByUser(1:string userid,2:map<string,string> queryMap),
	//首先获取当前用户所拥有的角色，然后标记某用户所拥有的角色
	map<string,string> getCuserUserRole(1:string curuserid,2:string userid,3:map<string,string> queryMap),
	//查看全部用户
	map<string,string> queUser(1:map<string,string> queryMap),
	//设置用户角色关系
	map<string,string> setUserRole(1:string userid,2:list<string> roleids),
	//验证某用户是否有某权限
	map<string,string> authorityFilter(1:string userid,2:string url,3:string project),
	//判断连接是否断开
	void ping()
}