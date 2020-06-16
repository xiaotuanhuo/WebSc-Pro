package sc.common.shiro.realm;

import sc.common.shiro.ShiroActionProperties;
import sc.common.util.ShiroUtil;
import sc.system.model.WebScUser;
import sc.system.service.UserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

/**
 * 根据用户名密码校验的 Realm.
 */
@Component
public class LoginNameRealm extends AuthorizingRealm {

    private static final Logger log = LoggerFactory.getLogger(LoginNameRealm.class);

    @Resource
    private UserService userService;

    @Resource
    private SessionDAO sessionDAO;

    @Resource
    private ShiroActionProperties shiroActionProperties;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("从数据库获取权限信息");
        WebScUser user = (WebScUser) principals.getPrimaryPrincipal();

        String loginnanme = user.getLoginName();

        Set<String> roles = userService.selectRoleNameByLoginName(loginnanme);
        Set<String> perms = userService.selectPermsByLoginName(loginnanme);

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(roles);
        authorizationInfo.setStringPermissions(perms);
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("从数据库获取认证信息");
        String loginname = (String) token.getPrincipal();
        WebScUser user = userService.selectOneByLoginName(loginname);
        if (user == null) {
            throw new UnknownAccountException();
        }
        // 如果账号被锁定, 则抛出异常, (超级管理员除外)
        if (ShiroUtil.USER_LOCK.equals(user.getStatus()) && !shiroActionProperties.getSuperAdminUsername().equals(loginname)) {
            throw new LockedAccountException();
        }
        return new SimpleAuthenticationInfo(user, user.getLoginPwd(), ByteSource.Util.bytes(user.getSalt()), super.getName());
    }

    public void clearAuthCacheByUserId(Integer userId) {
        // 获取所有 session
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        for (Session session : sessions) {
            // 获取 session 登录信息。
            Object obj = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (obj instanceof SimplePrincipalCollection) {
                // 强转
                SimplePrincipalCollection spc = (SimplePrincipalCollection) obj;
                WebScUser user = new WebScUser();
                BeanUtils.copyProperties(spc.getPrimaryPrincipal(), user);
                // 判断用户, 匹配用户ID.
                if (userId.equals(user.getUserId())) {
                    this.doClearCache(spc);
                }
            }
        }
    }

    public void clearAllAuthCache() {
        // 获取所有 session
        Collection<Session> sessions = sessionDAO.getActiveSessions();
        for (Session session : sessions) {
            // 获取 session 登录信息。
            Object obj = session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
            if (obj instanceof SimplePrincipalCollection) {
                // 强转
                SimplePrincipalCollection spc = (SimplePrincipalCollection) obj;
                WebScUser user = new WebScUser();
                BeanUtils.copyProperties(spc.getPrimaryPrincipal(), user);
                this.doClearCache(spc);
            }
        }
    }

    /**
     * 超级管理员拥有所有权限
     */
    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
    	WebScUser user = (WebScUser) principals.getPrimaryPrincipal();
        return shiroActionProperties.getSuperAdminUsername().equals(user.getLoginName()) || super.isPermitted(principals, permission);
    }

    /**
     * 超级管理员拥有所有角色
     */
    @Override
    public boolean hasRole(PrincipalCollection principals, String roleIdentifier) {
    	WebScUser user = (WebScUser) principals.getPrimaryPrincipal();
        return shiroActionProperties.getSuperAdminUsername().equals(user.getLoginName()) || super.hasRole(principals, roleIdentifier);
    }
}