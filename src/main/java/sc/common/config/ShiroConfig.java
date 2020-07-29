package sc.common.config;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.Filter;

import org.apache.commons.io.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import sc.common.shiro.EnhanceModularRealmAuthenticator;
import sc.common.shiro.RestShiroFilterFactoryBean;
import sc.common.shiro.ShiroActionProperties;
import sc.common.shiro.credential.RetryLimitHashedCredentialsMatcher;
import sc.common.shiro.filter.KickoutSessionFilter;
import sc.common.shiro.filter.RestAuthorizationFilter;
import sc.common.shiro.filter.RestFormAuthenticationFilter;
import sc.common.shiro.realm.LoginNameRealm;
import sc.common.shiro.realm.PhoneRealm;
import sc.system.model.vo.CDO;
import sc.system.model.vo.District;
import sc.system.service.ShiroService;
import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;

@Configuration
public class ShiroConfig {

	private static Map<String, CDO> cdoMap = new HashMap<String, CDO>();
	
    @Lazy
    @Resource
    private ShiroService shiroService;

    @Resource
    private ShiroActionProperties shiroActionProperties;

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer redisPort;

    @Bean
    public RestShiroFilterFactoryBean restShiroFilterFactoryBean(SecurityManager securityManager) {
        RestShiroFilterFactoryBean shiroFilterFactoryBean = new RestShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        filters.put("kickout", kickoutSessionFilter());
        filters.put("authc", new RestFormAuthenticationFilter());
        filters.put("perms", new RestAuthorizationFilter());

        Map<String, String> urlPermsMap = shiroService.getUrlPermsMap();
        shiroFilterFactoryBean.setFilterChainDefinitionMap(urlPermsMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 注入 securityManager
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager());
        securityManager.setRealms(Arrays.asList(loginNameRealm(), phoneRealm()));
        ModularRealmAuthenticator authenticator = new EnhanceModularRealmAuthenticator();
        securityManager.setAuthenticator(authenticator);
        authenticator.setRealms(Arrays.asList(loginNameRealm(), phoneRealm()));
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    /**
     * 手机号码验证码登陆Realm
     */
    @Bean
    public PhoneRealm phoneRealm() {
        return new PhoneRealm();
    }

    /**
     * 用户名密码登录 Realm
     */
    @Bean
    public LoginNameRealm loginNameRealm() {
    	LoginNameRealm loginNameRealm = new LoginNameRealm();
    	loginNameRealm.setCredentialsMatcher(hashedCredentialsMatcher());
    	loginNameRealm.setCacheManager(redisCacheManager());
        return loginNameRealm;
    }

    /**
     * 用户名密码登录密码匹配器
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        return new RetryLimitHashedCredentialsMatcher("md5");
    }

    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        redisCacheManager.setExpire(shiroActionProperties.getPermsCacheTimeout() == null ? 3600 : shiroActionProperties.getPermsCacheTimeout());
        redisCacheManager.setPrincipalIdFieldName("userId");
        return redisCacheManager;
    }

    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(redisHost + ":" + redisPort);
        return redisManager;
    }

    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setExpire(shiroActionProperties.getSessionTimeout() == null ? 1800 : shiroActionProperties.getSessionTimeout());
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setSessionInMemoryEnabled(false);
        return redisSessionDAO;
    }

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }
    
    @Bean(value = "district")
    public District district() {
    	District district = new District();
    	try {
    		// 读取数据权限json文件
    		ClassPathResource classPathResource = new ClassPathResource("/static/lib/layui/json/address.json");
    		String districtJson = IOUtils.toString(new InputStreamReader(classPathResource.getInputStream(), "UTF-8"));
    		JSONArray jsonArray = JSONObject.parseArray(districtJson);
    		Map<String, CDO> map = getDistrict(jsonArray, "0");		// 省级区划父节点为0
    		district.setDistrictMap(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return district;
    }
    
    /**
     * 递归获取所有行政区划
     * @param jsonArray
     * @param parentId
     * @return
     */
    private static Map<String, CDO> getDistrict(JSONArray jsonArray, String parentId) {
    	for (int i = 0; i < jsonArray.size(); i++) {
    		CDO cdo = new CDO();
    		String id = jsonArray.getJSONObject(i).getString("code");
    		cdo.setId(id);
    		cdo.setName(jsonArray.getJSONObject(i).getString("name"));
    		cdo.setParentId(parentId);
        	JSONArray recursiveArray = jsonArray.getJSONObject(i).getJSONArray("childs");
        	if (recursiveArray != null) {
				getDistrict(recursiveArray, id);
			}
        	cdoMap.put(id, cdo);
    	}
    	return cdoMap;
    }
    
    /**
	 *
	 * @描述：kickoutSessionFilter同一个用户多设备登录限制
	 * @创建人：wyait
	 * @创建时间：2018年4月24日 下午8:14:28
	 * @return
	 */
	public KickoutSessionFilter kickoutSessionFilter() {
		KickoutSessionFilter kickoutSessionFilter = new KickoutSessionFilter();
		// 使用cacheManager获取相应的cache来缓存用户登录的会话；用于保存用户—会话之间的关系的；
		// 这里我们还是用之前shiro使用的ehcache实现的cacheManager()缓存管理
		// 也可以重新另写一个，重新配置缓存时间之类的自定义缓存属性
		kickoutSessionFilter.setCacheManager(redisCacheManager());
//		kickoutSessionFilter.setCacheManager(ehCacheManager());
		// 用于根据会话ID，获取会话进行踢出操作的；
		kickoutSessionFilter.setSessionManager(sessionManager());
		// 是否踢出后来登录的，默认是false；即后者登录的用户踢出前者登录的用户；踢出顺序。
		kickoutSessionFilter.setKickoutAfter(false);
		// 同一个用户最大的会话数，默认1；比如2的意思是同一个用户允许最多同时两个人登录；
		kickoutSessionFilter.setMaxSession(1);
		// 被踢出后重定向到的地址；
		kickoutSessionFilter.setKickoutUrl("https://www.baidu.com/");
		return kickoutSessionFilter;
	}
}