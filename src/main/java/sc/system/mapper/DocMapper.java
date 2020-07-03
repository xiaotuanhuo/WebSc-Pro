package sc.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import sc.system.model.WebScDoc;
import sc.system.model.WebScOrganization;
import sc.system.model.WebScUser_Distribution;

@Mapper
public interface DocMapper {
	int insert(WebScDoc doc);
	
	int deleteByPrimaryKey(String documentId);
	
	WebScDoc selectByPrimaryKey(String documentId);

	int updateByPrimaryKey(WebScDoc doc);
	
	List<WebScDoc> selectWebScDocList(WebScDoc doc);
	
	WebScOrganization findWebScDocOrg(String documentId);
	
	List<String> getWorkDr(String time);
	
	List<WebScUser_Distribution> getDistributionDrGridList(Map<String, String> searchMap);
}
