namespace java com.inveno.cps.authenticate.thrift

service AuthenticateService {
	//登录
	map<string,string> login(1: string username,2: string password),
	//注册
	map<string,string> enroll(1: string username,2: string password,3: string registerCode),
	//设置密码
	map<string,string> setPassword(1: string password,2: string returnParam),
	//发送找回密码邮件
	map<string,string> getBack(1: string email,2: string checkCode,3: string checkCodeInServer,4: string url),
	//退出
	map<string,string> loginOut(2: string uuid),
	//重置密码
	map<string,string> resetPassword(1: string uuid,2: string password,3: string newPassword),
	//发送注册邮件
	map<string,string> sendEnrollEmail(1: string email),
	//邮件链接验证
	map<string,string> validGetBack(1: string returnParam),
	//获取用户信息
	map<string,string> getUserInfo(1: string uuid),
	//判断连接是否断开
	void ping()
}