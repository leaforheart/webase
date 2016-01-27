package com.inveno.cps.authenticate.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;

import com.inveno.cps.authenticate.dao.AuthenticateDao;
import com.inveno.cps.authenticate.model.UserModel;
import com.inveno.cps.authenticate.thrift.AuthenticateService;
import com.inveno.cps.common.baseclass.AbstractBaseService;
import com.inveno.cps.common.util.AuthCode;
import com.inveno.cps.common.util.Constants;
import com.inveno.cps.common.util.Dictionary;
import com.inveno.cps.common.util.PropertiesUtil;
import com.inveno.cps.common.util.SendMailUtil;
import com.inveno.cps.redis.RedisClient;
import com.inveno.util.CollectionUtils;
import com.inveno.util.MD5Utils;
import com.inveno.util.StringUtil;

/**
 * 登录认证服务类
 * 
 * @author XYL
 *
 */
public class AuthenticateServiceImpl extends AbstractBaseService implements AuthenticateService.Iface {
	private Logger log = Logger.getLogger(AuthenticateServiceImpl.class);

	private AuthenticateDao authenticateDao;

	public AuthenticateDao getAuthenticateDao() {
		return authenticateDao;
	}

	public void setAuthenticateDao(AuthenticateDao authenticateDao) {
		this.authenticateDao = authenticateDao;
	}

	/**
	 * 登录
	 */
	@Override
	public Map<String, String> login(String username, String password) {
		Jedis jedis = RedisClient.getJedis();
		Map<String, String> map = new HashMap<String, String>();
		try {
			List<String> para = new ArrayList<String>();
			para.add(username);
			List<UserModel> userList = authenticateDao.findByHql("from UserModel as user where user.username=?", para);
			if (CollectionUtils.isEmpty(userList)) {
				map.put(Constants.RETURN_CODE, "-1");// 账户不存在
				return map;
			}
			UserModel user = userList.get(0);
			String encodePassword = user.getPassword();
			if (!encodePassword.equals(MD5Utils.getResult(password))) {
				map.put(Constants.RETURN_CODE, "-2");// 密码错误
				return map;
			}
			int openClose = user.getOpenClose();
			if(openClose==0) {
				map.put(Constants.RETURN_CODE, "-3");// 该账户已关闭
				return map;
			}
			user.setOnline(true);
			String uuid = UUID.randomUUID().toString();
			System.out.println(uuid);
			jedis.hset(uuid, "userId", user.getId());
			jedis.hset(uuid, "userName", user.getUsername());
			String realName = user.getRealname();
			if (StringUtil.isEmpty(realName)) {
				realName = user.getUsername();
			} else {
				jedis.hset(uuid, "realName", user.getRealname());
			}
			jedis.expire(uuid, Integer.parseInt(PropertiesUtil.getProperty(Constants.REDIS_PATH, "sessionTime")));// 保存2个小时
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			map.put("realName", realName);
			map.put("userId", user.getId());
			map.put("cookieName", Constants.SESSION_IDENTIFIER);
			map.put("cookieValue", uuid);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.clear();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
		}finally {
			RedisClient.returnResource(jedis);
		}

		return map;
	}

	/**
	 * 注册(通过邮箱)
	 */
	@Override
	public Map<String, String> enroll(String username, String password, String registerCode) {
		Jedis jedis = RedisClient.getJedis();
		Map<String, String> map = new HashMap<String, String>();
		try {
			UserModel user = new UserModel();
			String redisCode = jedis.get("registerCode" + username);
			if (redisCode == null || !redisCode.equals(registerCode)) {
				map.put(Constants.RETURN_CODE, "-1");// 验证码错误
				return map;
			}
			List<String> parameters = new ArrayList<String>();
			parameters.add(username);
			String checkSql = "select count(1) from t_user where username=?";
			String isUserNameRepeat = isRepeat(checkSql, parameters);
			if (isUserNameRepeat != Constants.SUCCESS_CODE) {
				map.put(Constants.RETURN_CODE, "-2");// 账号已存在
				return map;
			}
			user.setUsername(username);
			user.setEmail(username);
			user.setState(Dictionary.USER_STATE_ENABLE);
			user.setPassword(MD5Utils.getResult(password));
			user.setCreateTime(new Date());
			user.setOpenClose(1);
			authenticateDao.save(user);

			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);// 注册成功
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.clear();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
		} finally {
			RedisClient.returnResource(jedis);
		}

		return map;
	}

	private String isRepeat(String sql, List<?> parameters) {
		List<Object> checkList = authenticateDao.findBySql(sql, parameters);
		Object[] obj = null;
		long count = 0;
		if (!CollectionUtils.isEmpty(checkList)) {
			obj = (Object[]) checkList.get(0);
			count = (Long) obj[0];
		}
		if (count > 0) {
			return "-1";
		}
		return Constants.SUCCESS_CODE;
	}

	@Override
	public Map<String, String> setPassword(String password, String returnParam) {

		Map<String, String> map = new HashMap<String, String>();
		try {
			Map<String, String> rMap = new HashMap<String, String>();
			rMap = validGetBack(returnParam);
			String userid = rMap.get("userId");
			if(StringUtil.isEmpty(userid)) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			if(StringUtil.isEmpty(password)) {
				map.put(Constants.RETURN_CODE, "-2");
				return map;
			}
			List<String> parameters = new ArrayList<String>();
			parameters.add(MD5Utils.getResult(password));
			parameters.add(userid);
			String resetPwdSql = "update t_user set password=? where user_id=?";
			authenticateDao.excuteSql(resetPwdSql, parameters);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.clear();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
		}

		return map;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Map<String, String> getBack(String email, String checkCode, String checkCodeInServer, String url) {
		Jedis jedis = RedisClient.getJedis();
		Map<String, String> map = new HashMap<String, String>();
		try {
			// 用户登录账号(邮箱)
			if (checkCode == null || !checkCode.equals(checkCodeInServer)) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}

			// 通过邮箱找到用户id
			String userId = findUserIdByEmail(email);
			if ("-1".equals(userId)) {
				map.put(Constants.RETURN_CODE, "-2");
				return map;
			}

			// 拿到发送邮件时间long
			Calendar cal = Calendar.getInstance();
			Long time = cal.getTimeInMillis() / 1000;
			// 通过密钥给id加密
			String idAndTime;
			try {
				idAndTime = URLEncoder.encode(AuthCode.authcodeEncode(userId + "-" + time, Constants.AUTHCODE_KEY),
						"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				map.put(Constants.RETURN_CODE, "-3");
				return map;
			}
			// 组装urlO
			String findpwdUrl = url + "?returnParam=" + idAndTime;
			// 组装邮件内容
			String mailContent = SendMailUtil.getMailTemplate(1);
			mailContent = mailContent.replaceAll("findpwdUrl", findpwdUrl);
			// 发送邮件到用户邮箱
			try {
				SendMailUtil.assembleMailAndSend("英威诺登录密码重置", mailContent, email);
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
				map.put(Constants.RETURN_CODE, "-4");// 邮件发送失败
				return map;
			}
			jedis.set(URLDecoder.decode(idAndTime), idAndTime);
			jedis.expire(idAndTime, 60 * 30);// 邮件有效期是30分钟
			map.put("returnParam", idAndTime);
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.clear();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
		}finally {
			RedisClient.returnResource(jedis);
		}

		return map;
	}

	private String findUserIdByEmail(String email) {
		List<String> para = new ArrayList<String>();
		para.add(email);
		List<UserModel> userList = authenticateDao.findByHql("from UserModel as user where user.username=?", para);
		if (CollectionUtils.isEmpty(userList)) {
			return "-1";
		}
		return userList.get(0).getId();
	}

	@Override
	public Map<String, String> loginOut(String uuid) {
		Jedis jedis = RedisClient.getJedis();
		Map<String, String> map = new HashMap<String, String>();
		try {
			String userid = jedis.hget(uuid, "userId");
			if (StringUtil.isEmpty(userid)) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			updateUserState(userid, "N");
			jedis.hdel(uuid, "userId");
			jedis.hdel(uuid, "userName");
			jedis.hdel(uuid, "realName");
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.clear();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
		} finally {
			RedisClient.returnResource(jedis);
		}

		return map;
	}

	private void updateUserState(String id, String flag) {
		List<Object> params = new ArrayList<Object>();
		params.add(flag);
		params.add(id);
		String sql = "update t_user set online=? where user_id=?";
		authenticateDao.excuteSql(sql, params);
	}

	@Override
	public Map<String, String> resetPassword(String uuid, String password, String newPassword) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			UserModel user = null;
			try {
				String userid = this.getUserInfo(uuid).get("userId");
				user = findByIdUser(userid);
			} catch (Exception e) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			if (user != null && user.getPassword() == null) {// 旧密码为空时，直接修改
				user.setPassword(MD5Utils.getResult(newPassword));
				authenticateDao.update(user);
				map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
				return map;
			} else if (user != null && user.getPassword().equals(MD5Utils.getResult(password))) {
				user.setPassword(MD5Utils.getResult(newPassword));
				authenticateDao.update(user);
				map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
				return map;
			}
			map.put(Constants.RETURN_CODE, "-2");
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.clear();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
		}

		return map;
	}

	private UserModel findByIdUser(String id) {
		return authenticateDao.findById(id, UserModel.class);
	}

	@Override
	public Map<String, String> sendEnrollEmail(String email) {
		Jedis jedis = RedisClient.getJedis();
		Map<String, String> map = new HashMap<String, String>();
		try {
			if (StringUtil.isEmpty(email)) {
				map.put(Constants.RETURN_CODE, "-1");
				return map;
			}
			List<String> parameters = new ArrayList<String>();
			parameters.add(email);
			String checkSql = "select count(1) from t_user where username=?";
			String isUserNameRepeat = isRepeat(checkSql, parameters);
			if (isUserNameRepeat != Constants.SUCCESS_CODE) {
				map.put(Constants.RETURN_CODE, "-2");
				return map;
			}
			String mailContent = SendMailUtil.getMailTemplate(2);
			String registerCode = String.valueOf(Math.round((Math.random() * 9 + 1) * 100000));
			mailContent = mailContent.replaceAll("registerCode", registerCode);
			// 发送邮件到用户邮箱
			try {
				SendMailUtil.assembleMailAndSend("英威诺注册码", mailContent, email);
			} catch (Exception e) {
				e.printStackTrace();
				map.put(Constants.RETURN_CODE, "-3");// 邮件发送失败
			}
			map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			jedis.set("registerCode" + email, registerCode);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.clear();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
		} finally {
			RedisClient.returnResource(jedis);
		}

		return map;
	}

	@Override
	public Map<String, String> validGetBack(String returnParam) {
		Jedis jedis = RedisClient.getJedis();
		HashMap<String, String> returnMap = new HashMap<String, String>();
		try {
			if (StringUtil.isEmpty(returnParam)) {
				returnMap.put(Constants.RETURN_CODE, "-1");
				return returnMap;
			}
			
			returnParam = returnParam.replace(" ", "+");
			
			if (jedis.get(returnParam) == null || StringUtil.isEmpty(jedis.get(returnParam))) {
				returnMap.put(Constants.RETURN_CODE, "-2");// 该链接已被使用或过期
				return returnMap;
			}

			
			String idAndTime = null;
			try {
				idAndTime = URLDecoder.decode(AuthCode.authcodeDecode(returnParam, Constants.AUTHCODE_KEY), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				returnMap.put(Constants.RETURN_CODE, "-3");
				return returnMap;
			}
			if (idAndTime.indexOf("-") < 0) {
				returnMap.put(Constants.RETURN_CODE, "-4");
				return returnMap;
			}

			String userName = findUserNameById(idAndTime.split("-")[0]); // 根据用户id获取用户名
			returnMap.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			returnMap.put("userName", userName);
			returnMap.put("userId", idAndTime.split("-")[0]);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
			returnMap.clear();
			returnMap.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
		} finally {
			RedisClient.returnResource(jedis);
		}

		return returnMap;

	}

	private String findUserNameById(String userId) {
		UserModel user = authenticateDao.findById(userId, UserModel.class);
		return user.getUsername();
	}

	@Override
	public Map<String, String> getUserInfo(String uuid) {
		Jedis jedis = RedisClient.getJedis();
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			if (StringUtil.isEmpty(uuid)) {
				map.put(Constants.RETURN_CODE, "-1");// 登录验证cookie为空
				return map;
			}

			if (StringUtil.isEmpty(jedis.hget(uuid, "userId"))) {
				map.put(Constants.RETURN_CODE, "-2");// session为空
			} else {
				String userId = jedis.hget(uuid, "userId");
				UserModel user = authenticateDao.findById(userId, UserModel.class);
				if (user == null) {
					map.put(Constants.RETURN_CODE, "-3");// 账户已不存在
					return map;
				}
				map.put("userId", userId);
				map.put("userName", jedis.hget(uuid, "userName"));
				String realName = jedis.hget(uuid, "realName");
				if (StringUtil.isEmpty(realName)) {
					realName = jedis.hget(uuid, "userName");
				}
				map.put("realName", realName);
				map.put(Constants.RETURN_CODE, Constants.SUCCESS_CODE);
			}

		} catch (Throwable e) {
			log.error(e.getMessage());
			e.printStackTrace();
			map.clear();
			map.put(Constants.RETURN_CODE, Constants.SERVER_CODE);
		} finally {
			RedisClient.returnResource(jedis);
		}

		return map;
	}

	@Override
	public void ping(){}

}
