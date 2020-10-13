package sc.system.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;

import sc.common.util.UUID19;
import sc.system.mapper.OperativeMapper;
import sc.system.model.WebScOperative;

@Service
public class OperativeService {
	@Resource
    private OperativeMapper operativeMapper;
	
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
		operative.setOperativeId(UUID19.uuid());
		operative.setUrgenttime(0);
		operativeMapper.insert(operative);
	}
	
	@Transactional
	public void update(WebScOperative operative) {
		operativeMapper.updateByPrimaryKey(operative);
	}
	
	@Transactional
    public void delete(String operativeId) {
		operativeMapper.deleteByPrimaryKey(operativeId);
	}
}
