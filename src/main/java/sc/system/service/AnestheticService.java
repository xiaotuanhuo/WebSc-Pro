package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;

import sc.common.util.UUID19;
import sc.system.mapper.AnestheticMapper;
import sc.system.model.WebScAnesthetic;

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
		anesthetic.setAnestheticId(UUID19.uuid());
		anestheticMapper.insert(anesthetic);
	}
	
	@Transactional
	public void update(WebScAnesthetic anesthetic) {
		anestheticMapper.updateByPrimaryKey(anesthetic);
	}
	
	@Transactional
    public void delete(String anestheticId) {
		anestheticMapper.deleteByPrimaryKey(anestheticId);
	}
}
