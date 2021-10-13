package sc.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import sc.system.model.WebScDoc;
import sc.system.model.WebScEvaluate;
import sc.system.model.WebScOrganization;
import sc.system.model.WebScUser;
import sc.system.model.WebScUser_Distribution;

@Mapper
public interface DocMapper {
	
	/**
	 * 修改订单状态
	 * @param documentId 订单号
	 * @return
	 */
	@Update("UPDATE WSC_DOCUMENT SET document_state=#{documentState} WHERE document_id=#{documentId}")
	int updDocumentState(@Param("documentState") String documentState, @Param("documentId") String documentId);
	
	/**
	 * 获取今天以前的状态为待完成的订单号
	 * @param documentState 订单状态
	 * @param ssjssj 手术结束时间
	 * @return
	 */
	@Select("SELECT doc.document_id FROM WSC_DOCUMENT doc, WSC_DOCUMENT_TMP tmp WHERE "
			+ "doc.document_id = tmp.document_id "
			+ "AND document_state=#{documentState} "
			+ "AND ssjssj<=#{ssjssj}")
	List<String> selectByDocumentState(@Param("documentState") String documentState, @Param("ssjssj") String ssjssj);
	
	/**
	 * 获取机构的平均评分
	 * @return
	 */
	@Select("SELECT AVG(doctor_evaluate) AS orgEvaluate,org_id AS orgId FROM WSC_DOCUMENT WHERE doctor_evaluate <> -1 GROUP BY org_id")
	List<Map<String, Object>> selectOrgAvgEvaluate();
	
	/**
	 * 获取医生的平均评分
	 * @return
	 */
	@Select("SELECT AVG(hospital_evaluate) AS doctorEvaluate,qa_user_id AS doctorId FROM WSC_DOCUMENT WHERE hospital_evaluate <> -1 GROUP BY qa_user_id")
	List<Map<String, Object>> selectDoctorAvgEvaluate();
	
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
	
	int insertWscEvaluate(WebScEvaluate wse);
	
	List<WebScDoc> selectWebScDocList(WebScDoc doc);
	
	WebScOrganization findWebScDocOrg(String documentId);
	
	List<String> getWorkDr(String time, String orgId);
	
	List<WebScUser_Distribution> getDistributionDrGridList(Map<String, String> searchMap);
	
	List<WebScUser> getTransferUser(Map<String, String> searchMap);
	
	List<WebScUser> getQaUserInfo(Map<String, String> searchMap);
	
	
	
	
	int statsToday(@Param("userId") String userId);
	int statsMonth(@Param("userId") String userId);
	int statsYear(@Param("userId") String userId);
	int statsSum(@Param("userId") String userId);
}
