package sc.system.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import sc.system.mapper.DocMapper;
import sc.system.model.WebScDoc;
import sc.system.model.WebScOrganization;
import sc.system.model.WebScUser;
import sc.system.model.WebScUser_Distribution;

import com.github.pagehelper.PageHelper;

@Service
public class DocService {
	@Resource
	private DocMapper docMapper;
	
	public int insert(WebScDoc doc) {
        return docMapper.insert(doc);
    }

    public int deleteByPrimaryKey(String documentId) {
        return docMapper.deleteByPrimaryKey(documentId);
    }

    public int updateByPrimaryKey(WebScDoc doc) {
    	return docMapper.updateByPrimaryKey(doc);
    }

    public WebScDoc selectByPrimaryKey(String documentId) {
        return docMapper.selectByPrimaryKey(documentId);
    }
    
    public List<WebScDoc> selectWebScDocList(int page, int rows, WebScDoc doc){
    	PageHelper.startPage(page, rows);
    	return docMapper.selectWebScDocList(doc);
    }
    
    public List<WebScUser> getDistributionDrGridList(String documentId, String province){
    	//获取订单数据
    	WebScDoc doc = new WebScDoc();
    	doc.setDocumentId(documentId);
    	List<WebScDoc> docList = docMapper.selectWebScDocList(doc);
    	if(docList != null){
	    	doc = docList.get(0);
	    	
	    	//获取医院信息
	    	WebScOrganization org = docMapper.findWebScDocOrg(documentId);
	    	
	    	//获取当天医院匹配的医生
	    	
	    	List<String> uls = docMapper.getWorkDr(doc.getOperateStartTime());
	    	
	    	//创建查询条件
	    	Map<String, String> searchMap = new HashMap<String, String>();
	    	searchMap.put("province", province);
	    	searchMap.put("org_id",org.getOrgId());
	    	searchMap.put("operate_start_time", doc.getOperateStartTime());
	    	//获取医生信息
	    	List<WebScUser_Distribution> drList = docMapper.getDistributionDrGridList(searchMap);
    	
    	}
    	return null;
    }
}
