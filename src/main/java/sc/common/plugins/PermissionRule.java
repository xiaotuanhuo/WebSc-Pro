package sc.common.plugins;

import java.util.HashMap;
import java.util.Map;

/**
 * 角色对应的权限code规则类
 * @author aisino
 *
 */
public class PermissionRule {

	/**
	 * Integer 角色id
	 * String 规则表达式
	 */
	private Map<String, String> ruleMap = new HashMap<String, String>();
	
	public Map<String, String> getRuleMap() {
		return ruleMap;
	}
	
	public void setRuleMap(Map<String, String> ruleMap) {
		this.ruleMap = ruleMap;
	}
	
}
