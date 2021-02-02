package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import sc.common.exception.DuplicateNameException;
import sc.common.util.Numbers;
import sc.common.util.UUID19;
import sc.system.mapper.OperativeMapper;
import sc.system.mapper.OperativeTypeMapper;
import sc.system.model.WebScOperative;
import sc.system.model.WscOperativeType;

@Service
public class OperativeService {
	private static final Logger log = LoggerFactory.getLogger(OperativeService.class);
	
	@Resource
    private OperativeMapper operativeMapper;
	
	@Resource
	private OperativeTypeMapper typeMapper;
	
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
	
	public void add(WebScOperative operative) {
		String operativeName = operative.getOperativeName().trim();	// 消除前后空格
		checkNameExistOnCreate(operativeName);
		operative.setOperativeName(operativeName);
		operative.setOperativeId(getRandomId());
		operative.setUrgenttime(0);
		
		int result = operativeMapper.insert(operative);
		if (result == 1) {
			String reqData = "{" + 
	        		"\"token\":\"" + "operativeluceneupdtoken1234" + "\"}";
			HttpResponse response = HttpRequest.post(indexUrl).header(Header.CONTENT_TYPE, "application/json")
					.header(Header.ACCEPT, "application/json").body(reqData).execute();
			if (response.getStatus() == 200) {
				log.info("更新索引文件请求成功");
			} else {
				log.info("更新索引文件请求失败:" + response.getStatus());
			}
		}
	}
	
	public void update(WebScOperative operative) {
		String operativeName = operative.getOperativeName().trim();	// 消除前后空格
		operative.setOperativeName(operativeName);
		checkNameExistOnUpdate(operative);
		int result = operativeMapper.updateByPrimaryKey(operative);
		if (result == 1) {
			String reqData = "{" + 
	        		"\"token\":\"" + "operativeluceneupdtoken1234" + "\"}";
			HttpResponse response = HttpRequest.post(indexUrl).header(Header.CONTENT_TYPE, "application/json")
					.header(Header.ACCEPT, "application/json").body(reqData).execute();
			if (response.getStatus() == 200) {
				log.info("更新索引文件请求成功");
			} else {
				log.info("更新索引文件请求失败:" + response.getStatus());
			}
		}
	}
	
	@Transactional
    public void delete(String operativeId) {
		int result = operativeMapper.deleteByPrimaryKey(operativeId);
		if (result == 1) {
			String reqData = "{" + 
	        		"\"token\":\"" + "operativeluceneupdtoken1234" + "\"}";
			HttpResponse response = HttpRequest.post(indexUrl).header(Header.CONTENT_TYPE, "application/json")
					.header(Header.ACCEPT, "application/json").body(reqData).execute();
			if (response.getStatus() == 200) {
				log.info("更新索引文件请求成功");
			} else {
				log.info("更新索引文件请求失败:" + response.getStatus());
			}
		}
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
	
	public List<WscOperativeType> getAllType() {
		return typeMapper.selectAll();
	}
	
	private String getRandomId() {
		String id = Numbers.randomThousand();
		WebScOperative operative = operativeMapper.selectOperativeById(id);
		if (operative != null) {
			id = getRandomId();
		}
		return id;
	}
}
