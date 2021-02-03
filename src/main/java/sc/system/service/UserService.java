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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sc.common.constants.PatientEnum;
import sc.common.constants.RoleEnum;
import sc.common.exception.DuplicateNameException;
import sc.common.shiro.ShiroActionProperties;
import sc.common.util.ResultBean;
import sc.common.util.ShiroUtil;
import sc.common.util.TreeUtil;
import sc.common.util.UUID19;
import sc.common.util.UploadUtil;
import sc.system.mapper.BureauMapper;
import sc.system.mapper.DeptMapper;
import sc.system.mapper.OperationMapper;
import sc.system.mapper.OrganizationMapper;
import sc.system.mapper.UserMapper;
import sc.system.mapper.UserRoleMapper;
import sc.system.model.WebScBureau;
import sc.system.model.WebScDept;
import sc.system.model.WebScMenu;
import sc.system.model.WebScOrganization;
import sc.system.model.WebScUser;
import sc.system.model.vo.UserVO;

import com.github.pagehelper.PageHelper;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

@Service
public class UserService {
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	
	@Resource
	private UserMapper userMapper;
	
	@Resource
	private DeptMapper deptMapper;
	
	@Resource
	private BureauMapper bureauMapper;
	
	@Resource
	private OrganizationMapper organizationMapper;
	
	@Resource
	private OperationMapper operationMapper;
	
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
	
	@Value("${default-image}")
	private String defaultImage;	// 默认头像名称
	
	@Value("${notify-path-user}")
	private String indexUrl;	// 更新索引通知地址
	
	public List<WebScUser> selectAllWithGroup(int page, int rows, WebScUser userQuery) {
		PageHelper.startPage(page, rows);
		Subject subject = SecurityUtils.getSubject();
		WebScUser user = (WebScUser) subject.getPrincipal();
		UserVO uvo = new UserVO();
		uvo.setUserId(user.getUserId());
		uvo.setName(userQuery.getUserName());
		uvo.setPhone(userQuery.getPhone());
		uvo.setRole(RoleEnum.valueOf(Integer.parseInt(user.getRoleId())).getType());
		uvo.setProvince(user.getProvince());
		uvo.setCity(user.getCity());
		uvo.setArea(user.getArea());
		
		// 医疗机构管理员仅可查看所属医疗机构内角色
		if (RoleEnum.valueOf(Integer.parseInt(user.getRoleId())) == RoleEnum.YLJGGLY) {
			WebScOrganization organization = organizationMapper.selectByPrimaryKey(user.getRoleTypeId());
			uvo.setOrganizationId(user.getRoleTypeId());
			uvo.setRootId(organization.getRootId());
			uvo.setLeaf(organization.getLeaf());
		}
		
		return userMapper.selectWithRoleAndDist(uvo);
	}
	
	public Integer[] selectRoleIdsById(String userId) {
		return userMapper.selectRoleIdsByUserId(userId);
	}
	
	public ResultBean upload(MultipartFile file, String uploadPath) {
		return UploadUtil.upload(file, uploadPath);
	}
	
	public String add(WebScUser user) {
		boolean index = false;	// 医生或者护士 更新索引文件
		checkLoginNameExistOnCreate(user.getLoginName());
		String salt = generateSalt();
		String encryptPassword = new Md5Hash(user.getLoginPwd(), salt).toString();
		
		user.setUserId(UUID19.uuid());
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
		if (user.getTitlesNo().equals("")) {
			user.setTitlesNo(null);
			user.setTitles(null);
		}
		switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
			case XTGLY:
			case CJGLY:
				user.setProvince(null);
				user.setCity(null);
				user.setArea(null);
				// 非医生护士角色清空项
				user.setIdCard(null);
				user.setCertificateNo(null);
				user.setOccupationalNo(null);
				user.setTitles(null);
				user.setTitlesNo(null);
				user.setPhoto(null);
				break;
			case QYGLY:
			case QYDDLRY:
				WebScDept dept = deptMapper.selectByPrimaryKey(user.getRoleTypeId());
				user.setProvince(dept.getProvince());
				user.setCity(dept.getCity());
				user.setArea(null);
				// 非医生护士角色清空项
				user.setIdCard(null);
				user.setCertificateNo(null);
				user.setOccupationalNo(null);
				user.setTitles(null);
				user.setTitlesNo(null);
				user.setPhoto(null);
				break;
			case YS:
			case HS:
				WebScDept dept2 = deptMapper.selectByPrimaryKey(user.getRoleTypeId());
				user.setProvince(dept2.getProvince());
				user.setCity(dept2.getCity());
				user.setArea(null);
				index = true;
				if (user.getPhoto().equals("")) {
					user.setPhoto(defaultImage);
				}
				break;
			case YLJGGLY:
			case JGDDLRY:
				WebScOrganization organization = organizationMapper.selectByPrimaryKey(user.getRoleTypeId());
				user.setProvince(organization.getProvince());
				user.setCity(organization.getCity());
				user.setArea(organization.getArea());
				// 非医生护士角色清空项
				user.setIdCard(null);
				user.setCertificateNo(null);
				user.setOccupationalNo(null);
				user.setTitles(null);
				user.setTitlesNo(null);
				user.setPhoto(null);
				break;
			case WJJGLY:
				WebScBureau bureau = bureauMapper.selectByPrimaryKey(user.getRoleTypeId());
				user.setProvince(bureau.getProvince());
				user.setCity(bureau.getCity());
				user.setArea(bureau.getArea());
				// 非医生护士角色清空项
				user.setIdCard(null);
				user.setCertificateNo(null);
				user.setOccupationalNo(null);
				user.setTitles(null);
				user.setTitlesNo(null);
				user.setPhoto(null);
				break;
			default:
				break;
		}
		int result = userMapper.insert(user);
		if (index && result == 1) {
			HttpResponse response = HttpRequest.post(indexUrl).header(Header.CONTENT_TYPE, "application/json")
					.header(Header.ACCEPT, "application/json").execute();
			if (response.getStatus() == 200) {
				log.info("更新索引文件请求成功");
			} else {
				log.info("更新索引文件请求失败:" + response.getStatus());
			}
		}
		return user.getUserId();
	}
	
	public void lock(String userId) {
		WebScUser user = userMapper.selectByPrimaryKey(userId);
		user.setStatus(ShiroUtil.LOCK);
		userMapper.updateByPrimaryKey(user);
	}
	
	public void unlock(String userId) {
		// 校验其所属机构是否处于锁定状态
		WebScUser user = userMapper.selectByPrimaryKey(userId);
		switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
			case CJGLY:
			case QYGLY:
			case YS:
			case HS:
			case QYDDLRY:
				WebScDept dept = deptMapper.selectByPrimaryKey(user.getRoleTypeId());
				if (dept.getStatus().equals(ShiroUtil.LOCK)) {
					throw new DuplicateNameException("所属机构已锁定，无法激活");
				}
				break;
			case YLJGGLY:
			case JGDDLRY:
				WebScOrganization organization = organizationMapper.selectByPrimaryKey(user.getRoleTypeId());
				if (organization.getStatus().equals(ShiroUtil.LOCK)) {
					throw new DuplicateNameException("所属机构已锁定，无法激活");
				}
				break;
			case WJJGLY:
				WebScBureau bureau = bureauMapper.selectByPrimaryKey(user.getRoleTypeId());
				if (bureau.getStatus().equals(ShiroUtil.LOCK)) {
					throw new DuplicateNameException("所属机构已锁定，无法激活");
				}
				break;
			default:
				break;
		}
		user.setStatus(ShiroUtil.UNLOCK);
		userMapper.updateByPrimaryKey(user);
	}
	
	public void updateLastLoginTimeByUsername(String loginname) {
		userMapper.updateLastLoginTimeByLoginName(loginname);
	}
	
	public boolean disableUserByID(String id) {
		offlineByUserId(id); // 加上这段代码, 禁用用户后, 会将当前在线的用户立即踢出.
		return userMapper.updateStatusByPrimaryKey(id, "0") == 1;
	}
	
	public boolean enableUserByID(String id) {
		return userMapper.updateStatusByPrimaryKey(id, "1") == 1;
	}
	
	/**
	 * 根据用户 ID 激活账号.
	 * 
	 * @param userId
	 *            用户 ID
	 */
	public void activeUserByUserId(String userId) {
		userMapper.activeUserByUserId(userId);
	}
	
	public boolean update(WebScUser user) {
		boolean index = false;
		boolean result = false;
		checkLoginNameExistOnUpdate(user);
		if (user.getArea().equals("")) {
			user.setArea(null);
		}
		if (user.getCity().equals("")) {
			user.setCity(null);
		}
		if (user.getProvince().equals("")) {
			user.setProvince(null);
		}
		if (user.getTitlesNo().equals("")) {
			user.setTitlesNo(null);
			user.setTitles(null);
		}
		switch (RoleEnum.valueOf(Integer.parseInt(user.getRoleId()))) {
			case XTGLY:
			case CJGLY:
				user.setProvince(null);
				user.setCity(null);
				user.setArea(null);
				// 非医生护士角色清空项
				user.setIdCard(null);
				user.setCertificateNo(null);
				user.setOccupationalNo(null);
				user.setTitles(null);
				user.setTitlesNo(null);
				user.setPhoto(null);
				break;
			case QYGLY:
			case QYDDLRY:
				WebScDept dept = deptMapper.selectByPrimaryKey(user.getRoleTypeId());
				user.setProvince(dept.getProvince());
				user.setCity(null);
				user.setArea(null);
				// 非医生护士角色清空项
				user.setIdCard(null);
				user.setCertificateNo(null);
				user.setOccupationalNo(null);
				user.setTitles(null);
				user.setTitlesNo(null);
				user.setPhoto(null);
				break;
			case YS:
			case HS:
				WebScDept dept2 = deptMapper.selectByPrimaryKey(user.getRoleTypeId());
				user.setProvince(dept2.getProvince());
				user.setCity(null);
				user.setArea(null);
				index = true;
				if (user.getPhoto().equals("")) {
					user.setPhoto(defaultImage);
				}
				break;
			case YLJGGLY:
			case JGDDLRY:
				WebScOrganization organization = organizationMapper.selectByPrimaryKey(user.getRoleTypeId());
				user.setProvince(organization.getProvince());
				user.setCity(organization.getCity());
				user.setArea(organization.getArea());
				// 非医生护士角色清空项
				user.setIdCard(null);
				user.setCertificateNo(null);
				user.setOccupationalNo(null);
				user.setTitles(null);
				user.setTitlesNo(null);
				user.setPhoto(null);
				break;
			case WJJGLY:
				WebScBureau bureau = bureauMapper.selectByPrimaryKey(user.getRoleTypeId());
				user.setProvince(bureau.getProvince());
				user.setCity(bureau.getCity());
				user.setArea(bureau.getArea());
				// 非医生护士角色清空项
				user.setIdCard(null);
				user.setCertificateNo(null);
				user.setOccupationalNo(null);
				user.setTitles(null);
				user.setTitlesNo(null);
				user.setPhoto(null);
				break;
			default:
				break;
		}
		result = userMapper.updateByUser(user) == 1;
		if (index && result) {
			HttpResponse response = HttpRequest.post(indexUrl).header(Header.CONTENT_TYPE, "application/json")
			.header(Header.ACCEPT, "application/json").execute();
			if (response.getStatus() == 200) {
				log.info("更新索引文件请求成功");
			} else {
				log.info("更新索引文件请求失败:" + response.getStatus());
			}
		}
		return result;
	}
	
	public WebScUser selectOne(String id) {
		return userMapper.selectByPrimaryKey(id);
	}
	
	/**
	 * 新增时校验用户名是否重复
	 * @param loginname
	 *            用户名
	 */
	public void checkLoginNameExistOnCreate(String loginname) {
		if (userMapper.countByLoginName(loginname) > 0) {
			throw new DuplicateNameException("用户名已存在");
		}
	}
	
	public void checkLoginNameExistOnUpdate(WebScUser user) {
		if (userMapper.countByLoginNameNotIncludeUserId(user.getLoginName(), user.getUserId()) > 0) {
			throw new DuplicateNameException("用户名已存在");
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
	public void offlineByUserId(String userId) {
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
	public void grantRole(String userId, Integer[] roleIds) {
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
	public void delete(String userId) {
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
	
	public void updatePasswordByUserId(String userId, String password) {
		String salt = generateSalt();
		String encryptPassword = new Md5Hash(password, salt).toString();
		userMapper.updatePasswordByUserId(userId, encryptPassword, salt);
	}
	
	private String generateSalt() {
		return String.valueOf(System.currentTimeMillis());
	}
	
	/**
	 * 根据病人类型标识返回描述
	 * @param patientType
	 * @return
	 */
	public List<String> getPatients(String patientType) {
		List<String> patients = new ArrayList<String>();
		if (patientType != null) {
			String[] codes = patientType.split(",");
			for (String code : codes) {
				PatientEnum pe = PatientEnum.valueOf(Integer.parseInt(code));
				patients.add(pe.getType());
			}
		}
		return patients;
	}
	
	public List<String> getOperations(String operationType) {
		List<String> operations = new ArrayList<String>();
		if (operationType != null) {
			String[] codes = operationType.split(",");
			operations = operationMapper.selectByIds(codes);
		}
		return operations;
	}
}