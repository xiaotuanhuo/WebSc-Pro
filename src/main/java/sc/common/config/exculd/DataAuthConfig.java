package sc.common.config.exculd;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;

import sc.common.plugins.PermissionHelper;
import sc.common.plugins.PermissionRule;

/**
 * 数据权限规则配置类
 * @author aisino
 *
 */
@Configuration
@AutoConfigureAfter(PageHelperAutoConfiguration.class)
public class DataAuthConfig {

	private Map<String, JSONArray> jsonMap = new HashMap<String, JSONArray>();
	private static Map<String, PermissionRule> dataAuthMap = new HashMap<String, PermissionRule>();
	
	@Autowired
	private List<SqlSessionFactory> sqlSessionFactoryList;
	
	@PostConstruct
	public void addMyInterceptor() {
		try {
			// 读取数据权限json文件
			ClassPathResource classPathResource = new ClassPathResource("/static/data/dataAuth.json");
			String str = IOUtils.toString(new InputStreamReader(classPathResource.getInputStream(), "UTF-8"));
			jsonMap = JSONObject.parseObject(str, HashMap.class);
			// 遍历ruleMap 将权限code对应的规则转换成对象
			for (Map.Entry<String, JSONArray> entry : jsonMap.entrySet()) {
				PermissionRule permissionRule = new PermissionRule();
				Map<Integer, String> ruleMap = new HashMap<Integer, String>();
				JSONArray ruleArray = entry.getValue();
				for (int i = 0; i < ruleArray.size(); i++) {
					JSONObject jsonObject = (JSONObject) ruleArray.get(i);
					ruleMap.put(jsonObject.getInteger("role"), jsonObject.getString("exps"));
				}
				permissionRule.setRuleMap(ruleMap);
				dataAuthMap.put(entry.getKey(), permissionRule);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PermissionHelper ph = new PermissionHelper();
		for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
			sqlSessionFactory.getConfiguration().addInterceptor(ph);
		}
	}
	
	public static Map<String, PermissionRule> getDataAuthMap() {
		return dataAuthMap;
	}

}
