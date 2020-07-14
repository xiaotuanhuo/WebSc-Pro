package sc.system.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sc.common.constants.RoleEnum;
import sc.common.exception.DuplicateNameException;
import sc.common.shiro.ShiroActionProperties;
import sc.common.util.TreeUtil;
import sc.system.mapper.UserMapper;
import sc.system.mapper.UserRoleMapper;
import sc.system.model.WebScMenu;
import sc.system.model.WebScUser;
import sc.system.model.vo.UserVO;

import com.alipay.api.domain.UboVO;
import com.github.pagehelper.PageHelper;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Resource
	private UserMapper userMapper;
	
	@Resource
	private MenuService menuService;
	
	@Resource
	private UserRoleMapper userRoleMapper;
	
	@Resource
	private UserAuthsService userAuthsService;
	
	@Resource
	private SessionDAO sessionDAO;
	
	@Resource
	private ShiroActionProperties shiroActionProperties;
	
	public List<WebScUser> selectAllWithGroup(int page, int rows, WebScUser userQuery) {
		PageHelper.startPage(page, rows);
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		UserVO uvo = new UserVO();
		uvo.setUserId(user.getUserId());
		uvo.setName(userQuery.getUserName());
		uvo.setPhone(userQuery.getPhone());
		uvo.setRole(RoleEnum.valueOf(Integer.parseInt(user.getRoleId())).getType());
		return userMapper.selectWithRoleAndDist(uvo);
	}
	
	public Integer[] selectRoleIdsById(Integer userId) {
		return userMapper.selectRoleIdsByUserId(userId);
	}
	
	@Transactional
	public Integer add(WebScUser user) {
		checkLoginNameExistOnCreate(user.getLoginName());
		String salt = generateSalt();
		String encryptPassword = new Md5Hash(user.getLoginPwd(), salt).toString();
		
		user.setSalt(salt);
		user.setLoginPwd(encryptPassword);
		if (user.getArea().equals("")) {
			user.setArea(null);
		}
		if (user.getCity().equals("")) {
			user.setCity(null);
		}
		if (user.getProvince().equals("")) {
			user.setProvince(null);
		}
		userMapper.insert(user);
		
		return user.getUserId();
	}
	
	public void updateLastLoginTimeByUsername(String loginname) {
		userMapper.updateLastLoginTimeByLoginName(loginname);
	}
	
	public boolean disableUserByID(Integer id) {
		// offlineByUserId(id); // 加上这段代码, 禁用用户后, 会将当前在线的用户立即踢出.
		return userMapper.updateStatusByPrimaryKey(id, 0) == 1;
	}
	
	public boolean enableUserByID(Integer id) {
		return userMapper.updateStatusByPrimaryKey(id, 1) == 1;
	}
	
	/**
	 * 根据用户 ID 激活账号.
	 * 
	 * @param userId
	 *            用户 ID
	 */
	public void activeUserByUserId(Integer userId) {
		userMapper.activeUserByUserId(userId);
	}
	
	@Transactional
	public boolean update(WebScUser user, Integer[] roleIds) {
		checkLoginNameExistOnUpdate(user);
		grantRole(user.getUserId(), roleIds);
		return userMapper.updateByPrimaryKey(user) == 1;
	}
	
	public WebScUser selectOne(Integer id) {
		return userMapper.selectByPrimaryKey(id);
	}
	
	/**
	 * 新增时校验用户名是否重复
	 * @param loginname
	 *            用户名
	 */
	public void checkLoginNameExistOnCreate(String loginname) {
		if (userMapper.countByLoginName(loginname) > 0) {
			throw new DuplicateNameException();
		}
	}
	
	public void checkLoginNameExistOnUpdate(WebScUser user) {
		if (userMapper.countByLoginNameNotIncludeUserId(user.getLoginName(), user.getUserId()) > 0) {
			throw new DuplicateNameException();
		}
	}
	
	public void offlineBySessionId(String sessionId) {
		Session session = sessionDAO.readSession(sessionId);
		if (session != null) {
			log.debug("成功踢出 sessionId 为 :" + sessionId + "的用户.");
			session.stop();
			sessionDAO.delete(session);
		}
	}
	
	/**
	 * 删除所有此用户的在线用户
	 */
	public void offlineByUserId(Integer userId) {
		Collection<Session> activeSessions = sessionDAO.getActiveSessions();
		for (Session session : activeSessions) {
			SimplePrincipalCollection simplePrincipalCollection = (SimplePrincipalCollection) session
					.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
			if (simplePrincipalCollection != null) {
				WebScUser user = (WebScUser) simplePrincipalCollection.getPrimaryPrincipal();
				if (user != null && userId.equals(user.getUserId())) {
					offlineBySessionId(String.valueOf(session.getId()));
				}
			}
		}
	}
	
	@Transactional
	public void grantRole(Integer userId, Integer[] roleIds) {
		if (roleIds == null || roleIds.length == 0) {
			throw new IllegalArgumentException("赋予的角色数组不能为空.");
		}
		// 清空原有的角色, 赋予新角色.
		userRoleMapper.deleteUserRoleByUserId(userId);
		userRoleMapper.insertList(userId, roleIds);
	}
	
	public int count() {
		return userMapper.count();
	}
	
	@Transactional
	public void delete(Integer userId) {
		// 检查删除的是否是超级管理员, 如果是, 则不允许删除.
		WebScUser user = userMapper.selectByPrimaryKey(userId);
		if (shiroActionProperties.getSuperAdminUsername().equals(user.getLoginName())) {
			throw new UnauthorizedException("试图删除超级管理员, 被禁止.");
		}
		// userAuthsService.deleteByUserId(userId);
		userMapper.deleteByPrimaryKey(userId);
		userRoleMapper.deleteUserRoleByUserId(userId);
	}
	
	/**
	 * 获取用户拥有的所有菜单权限和操作权限.
	 * @param loginname
	 *            用户名
	 * @return 权限标识符号列表
	 */
	public Set<String> selectPermsByLoginName(String loginname) {
		Set<String> perms = new HashSet<>();
		List<WebScMenu> menuTreeVOS = menuService.selectMenuTreeVOByUsername(loginname);
		List<WebScMenu> leafNodeMenuList = new ArrayList<WebScMenu>();
		if (menuTreeVOS != null)
			leafNodeMenuList = TreeUtil.getAllLeafNode(menuTreeVOS);
		for (WebScMenu menu : leafNodeMenuList) {
			perms.add(menu.getPerms());
		}
		perms.addAll(userMapper.selectOperatorPermsByLoginName(loginname));
		return perms;
	}
	
	public Set<String> selectRoleNameByLoginName(String loginname) {
		return userMapper.selectRoleNameByLoginName(loginname);
	}
	
	public WebScUser selectOneByLoginName(String loginname) {
		return userMapper.selectOneByLoginName(loginname);
	}
	
	public void updatePasswordByUserId(Integer userId, String password) {
		String salt = generateSalt();
		String encryptPassword = new Md5Hash(password, salt).toString();
		userMapper.updatePasswordByUserId(userId, encryptPassword, salt);
	}
	
	private String generateSalt() {
		return String.valueOf(System.currentTimeMillis());
	}
}