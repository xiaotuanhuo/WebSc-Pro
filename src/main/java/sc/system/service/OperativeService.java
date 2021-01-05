package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import sc.common.exception.DuplicateNameException;
import sc.common.util.UUID19;
import sc.system.mapper.OperativeMapper;
import sc.system.model.WebScOperative;

@Service
public class OperativeService {
	@Resource
    private OperativeMapper operativeMapper;
	
	@Value("${notify-path-operative}")
	private String indexUrl;	// 更新索引通知地址
	
	public List<WebScOperative> getWebScOperativeList(String operativeName){
		return operativeMapper.getWebScOperativeList(operativeName);
	}
	
	public List<WebScOperative> getPageList(int page, int rows, WebScOperative query) {
		PageHelper.startPage(page, rows);
		return operativeMapper.selectOnPage(query);
	}
	
	public WebScOperative getOperativeById(String operativeId) {
		return operativeMapper.selectOperativeById(operativeId);
	}
	
	@Transactional
	public void add(WebScOperative operative) {
		String operativeName = operative.getOperativeName().trim();	// 消除前后空格
		checkNameExistOnCreate(operativeName);
		operative.setOperativeName(operativeName);
		operative.setOperativeId(UUID19.uuid());
		operative.setUrgenttime(0);
		
		int result = operativeMapper.insert(operative);
		if (result == 1) {
			String reqData = "{" + 
	        		"\"token\":\"" + "operativeluceneupdtoken1234" + "\"}";
			String data = HttpRequest.post(indexUrl)
					.header(Header.CONTENT_TYPE, "application/json")
					.header(Header.ACCEPT, "application/json")
					.body(reqData)
					.execute().body();
		}
	}
	
	@Transactional
	public void update(WebScOperative operative) {
		String operativeName = operative.getOperativeName().trim();	// 消除前后空格
		operative.setOperativeName(operativeName);
		checkNameExistOnUpdate(operative);
		int result = operativeMapper.updateByPrimaryKey(operative);
		if (result == 1) {
			String reqData = "{" + 
	        		"\"token\":\"" + "operativeluceneupdtoken1234" + "\"}";
			String data = HttpRequest.post(indexUrl)
					.header(Header.CONTENT_TYPE, "application/json")
					.header(Header.ACCEPT, "application/json")
					.body(reqData)
					.execute().body();
		}
	}
	
	@Transactional
    public void delete(String operativeId) {
		operativeMapper.deleteByPrimaryKey(operativeId);
	}
	
	/**
	 * 新增时校验手术名称是否重复
	 * @param operativeName
	 */
	public void checkNameExistOnCreate(String operativeName) {
		if (operativeMapper.countByName(operativeName) > 0) {
			throw new DuplicateNameException("手术名称已存在");
		}
	}
	
	public void checkNameExistOnUpdate(WebScOperative operative) {
		if (operativeMapper.countByNameNotIncludeId(operative.getOperativeName(), operative.getOperativeId()) > 0) {
			throw new DuplicateNameException("手术名称已存在");
		}
	}
}
