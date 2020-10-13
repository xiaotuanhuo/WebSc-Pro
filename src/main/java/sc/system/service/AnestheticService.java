package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;

import sc.common.exception.DuplicateNameException;
import sc.common.util.UUID19;
import sc.system.mapper.AnestheticMapper;
import sc.system.model.WebScAnesthetic;
import sc.system.model.WebScOperative;

@Service
public class AnestheticService {
	@Resource
    private AnestheticMapper anestheticMapper;
	
	public List<WebScAnesthetic> getWebScAnestheticList(String anestheticName){
		return anestheticMapper.getWebScAnestheticList(anestheticName);
	}
	
	public List<WebScAnesthetic> getPageList(int page, int rows, WebScAnesthetic query) {
		PageHelper.startPage(page, rows);
		return anestheticMapper.selectOnPage(query);
	}
	
	public WebScAnesthetic getAnestheticById(String anestheticId) {
		return anestheticMapper.selectAnestheticById(anestheticId);
	}
	
	@Transactional
	public void add(WebScAnesthetic anesthetic) {
		String anestheticName = anesthetic.getAnestheticName();	// 去除前后空格
		checkNameExistOnCreate(anestheticName);
		anesthetic.setAnestheticName(anestheticName);
		anesthetic.setAnestheticId(UUID19.uuid());
		anestheticMapper.insert(anesthetic);
	}
	
	@Transactional
	public void update(WebScAnesthetic anesthetic) {
		String anestheticName = anesthetic.getAnestheticName();	// 去除前后空格
		anesthetic.setAnestheticName(anestheticName);
		checkNameExistOnUpdate(anesthetic);
		anestheticMapper.updateByPrimaryKey(anesthetic);
	}
	
	@Transactional
    public void delete(String anestheticId) {
		anestheticMapper.deleteByPrimaryKey(anestheticId);
	}
	
	/**
	 * 新增时校验手术名称是否重复
	 * @param anestheticName
	 */
	public void checkNameExistOnCreate(String anestheticName) {
		if (anestheticMapper.countByName(anestheticName) > 0) {
			throw new DuplicateNameException("麻醉名称已存在");
		}
	}
	
	public void checkNameExistOnUpdate(WebScAnesthetic anesthetic) {
		if (anestheticMapper.countByNameNotIncludeId(anesthetic.getAnestheticName(), anesthetic.getAnestheticId()) > 0) {
			throw new DuplicateNameException("麻醉名称已存在");
		}
	}
}
