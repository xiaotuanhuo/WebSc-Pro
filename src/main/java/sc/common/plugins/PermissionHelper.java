package sc.common.plugins;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sc.common.annotation.DataAuth;
import sc.common.config.exculd.DataAuthConfig;

@Intercepts({@Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class})})
public class PermissionHelper implements Interceptor {
	private static final Logger log = LoggerFactory.getLogger(PermissionHelper.class);
	
	private static final ThreadLocal<Long> IGNORE_PERMISSION = new ThreadLocal<Long>();
	private static final ThreadLocal<Long> IGNORE_PERMISSION_BEGIN_AND_END = new ThreadLocal<>();
	private static final ThreadLocal<String> IGNORE_PERMISSION_TAG = new ThreadLocal<String>();
	private static final ThreadLocal<Long> IGNORE_JOIN_TABLE_PERMISSION_BEGIN_AND_END = new ThreadLocal<>();// 忽略掉子表权限
	private static final ThreadLocal<Long> IGNORE_JOIN_TABLE_PERMISSION = new ThreadLocal<>();// 忽略掉子表权限
	private static int MAPPED_STATEMENT_INDEX = 0;
	private static int PARAMETER_INDEX = 1;
    
	private static Map<String, PermissionRule> dataAuthMap = new HashMap<String, PermissionRule>();
	static {
		dataAuthMap = DataAuthConfig.getDataAuthMap();
	}
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (IGNORE_PERMISSION_BEGIN_AND_END.get() == null) {
			if (IGNORE_PERMISSION.get() == null) {
				processIntercept(invocation);
			} else {
				IGNORE_PERMISSION.remove();
			}
		} else {
			// 同时使用则以ignorePermissionBeginAndEnd为主，并将ignorePermission抹除
			if (IGNORE_PERMISSION.get() != null) {
				IGNORE_PERMISSION.remove();
			}
		}
		
		return invocation.proceed();
	}
	
	@Override
	public Object plugin(Object target) {
//		if (target instanceof StatementHandler) {
//			return Plugin.wrap(target, this);
//		} else {
//			return target;
//		}
		return Plugin.wrap(target, this);
	}
	
	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub
		
	}
	
	private void processIntercept(Invocation invocation) {
		try {
			PermissionRule permissionRule = dataAuthMap.get("dataAuthCode2");
			String exps = permissionRule.getRuleMap().get(3);
			
			MappedStatement ms = (MappedStatement) invocation.getArgs()[MAPPED_STATEMENT_INDEX];
			DataAuth dataAuth = getPermissionByDelegate(ms);
			if (dataAuth == null) {
				return;
			}
		} catch (Exception e) {
			log.error("处理Sql出错！", e);
		}
	}
	
	/**
	 * 获取数据权限注解信息
	 * 
	 * @param mappedStatement
	 * @return
	 */
	private DataAuth getPermissionByDelegate(MappedStatement mappedStatement) {
		DataAuth dataAuth = null;
		try {
			String id = mappedStatement.getId();
			String className = id.substring(0, id.lastIndexOf("."));
			String methodName = id.substring(id.lastIndexOf(".") + 1, id.length());
			final Class<?> cls = Class.forName(className);
			final Method[] method = cls.getMethods();
			for (Method me : method) {
				if (me.getName().equals(methodName) && me.isAnnotationPresent(DataAuth.class)) {
					dataAuth = me.getAnnotation(DataAuth.class);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataAuth;
	}
}
