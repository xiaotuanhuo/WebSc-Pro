package sc.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import sc.system.model.WebScDoc;
import sc.system.model.WebScOrganization;
import sc.system.model.WebScUser;
import sc.system.model.WebScUser_Distribution;

@Mapper
public interface DocMapper {
	
	List<WebScDoc> selectImportDocsByConditions(Map<String, Object> paraMap);
	
	@Select("SELECT '1' FROM WSC_DOCUMENT WHERE org_id=#{orgId} AND operative_id=#{operativeId} AND operate_start_time=#{operativeDate} AND patient_name=#{patientName}")
	String isExists(
			@Param("patientName") String patientName, 
			@Param("operativeDate") String operativeDate, 
			@Param("orgId") String orgId, 
			@Param("operativeId") String operativeId);
	
	int insert(WebScDoc doc);
	
	int deleteByPrimaryKey(String documentId);
	
	WebScDoc selectByPrimaryKey(String documentId);

	int updateByPrimaryKey(WebScDoc doc);
	
	List<WebScDoc> selectWebScDocList(WebScDoc doc);
	
	WebScOrganization findWebScDocOrg(String documentId);
	
	List<String> getWorkDr(String time);
	
	List<WebScUser_Distribution> getDistributionDrGridList(Map<String, String> searchMap);
	
	List<WebScUser> getTransferUser(Map<String, String> searchMap);
	
	List<WebScUser> getQaUserInfo(Map<String, String> searchMap);
	
}
