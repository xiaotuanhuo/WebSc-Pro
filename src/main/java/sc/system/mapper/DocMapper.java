package sc.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import sc.system.model.WebScDoc;
import sc.system.model.WebScOrganization;
import sc.system.model.WebScUser;
import sc.system.model.WebScUser_Distribution;

@Mapper
public interface DocMapper {
	@Select("SELECT * FROM WSC_DOCUMENT WHERE org_id=#{orgId} AND document_state=#{state} ORDER BY operate_start_time DESC LIMIT ${limit}")
	List<WebScDoc> selectWebScDocs(
			@Param("orgId") String orgId, 
			@Param("state") String state, 
			@Param("limit") int limit);
	
	@Select("SELECT COUNT(*) FROM WSC_DOCUMENT WHERE org_id=#{orgId} AND operate_start_time>=#{startDate} AND operate_start_time<#{endDate}")
	int selectOperativeCount(
			@Param("orgId") String orgId, 
			@Param("startDate") String startDate, 
			@Param("endDate") String endDate);
	
	@Update("UPDATE WSC_DOCUMENT SET hospital_evaluate=${evaluateStar},hospital_evaluate_memo=#{evaluateText} WHERE document_id=#{documentId}")
	int updDocById(@Param("documentId") String documentId, 
			@Param("evaluateText") String evaluateText, 
			@Param("evaluateStar") int evaluateStar);
	
	List<WebScDoc> selectXcxDocsByConditions(Map<String, Object> paraMap);
	
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
	
	
	
	
	int statsToday(@Param("userId") String userId);
	int statsMonth(@Param("userId") String userId);
	int statsYear(@Param("userId") String userId);
	int statsSum(@Param("userId") String userId);
}
