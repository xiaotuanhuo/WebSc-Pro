package sc.system.model;

import java.io.Serializable;

public class WebScDoc implements Serializable {
	/**   
	 * @Fields serialVersionUID : TODO
	 *
	*/
	private static final long serialVersionUID = 495917542461743605L;
	
	private String documentId;
	private String operativeId;
	private String operativeName;
	private String anestheticId;
	private String anestheticName;
	private String patientName;
	private Integer patientAge;;
	private String patientSex;
	private String patientNum;
	private String patientBednum;
	private String patienttypeId;
	private String operationtypeId;
	private String documentTitle;
	private String documentType;
	private String operateUser;
	private String operateQide;
	private String applyUserId;
	private String adminUserId;
	private String qaUserId;
	private String qaTeamId;
	private String hospitalMemo;
	private String qaMemo;
	private String adminMemo;
	private String memo;
	private String documentState;
	private String operateStartTime;
	private String operateEndTime;
	private String createDate;
	private Integer hospitalEvaluate;
	private String hospitalEvaluateMemo;
	private Integer doctorEvaluate;
	private String doctorEvaluateMemo;
	
	//单据发布者所在省，市，区
	private String province;
	private String city;
	private String area;
	
	//角色【临时判断用】
	private String roleId;
	
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public String getOperativeId() {
		return operativeId;
	}
	public void setOperativeId(String operativeId) {
		this.operativeId = operativeId;
	}
	public String getAnestheticId() {
		return anestheticId;
	}
	public void setAnestheticId(String anestheticId) {
		this.anestheticId = anestheticId;
	}
	public String getPatientName() {
		return patientName;
	}
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}
	public Integer getPatientAge() {
		return patientAge;
	}
	public void setPatientAge(Integer patientAge) {
		this.patientAge = patientAge;
	}
	public String getPatientNum() {
		return patientNum;
	}
	public void setPatientNum(String patientNum) {
		this.patientNum = patientNum;
	}
	public String getPatientBednum() {
		return patientBednum;
	}
	public void setPatientBednum(String patientBednum) {
		this.patientBednum = patientBednum;
	}
	public String getPatienttypeId() {
		return patienttypeId;
	}
	public void setPatienttypeId(String patienttypeId) {
		this.patienttypeId = patienttypeId;
	}
	public String getOperationtypeId() {
		return operationtypeId;
	}
	public void setOperationtypeId(String operationtypeId) {
		this.operationtypeId = operationtypeId;
	}
	public String getDocumentTitle() {
		return documentTitle;
	}
	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getOperateUser() {
		return operateUser;
	}
	public void setOperateUser(String operateUser) {
		this.operateUser = operateUser;
	}
	public String getOperateQide() {
		return operateQide;
	}
	public void setOperateQide(String operateQide) {
		this.operateQide = operateQide;
	}
	public String getApplyUserId() {
		return applyUserId;
	}
	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}
	public String getAdminUserId() {
		return adminUserId;
	}
	public void setAdminUserId(String adminUserId) {
		this.adminUserId = adminUserId;
	}
	public String getQaUserId() {
		return qaUserId;
	}
	public void setQaUserId(String qaUserId) {
		this.qaUserId = qaUserId;
	}
	public String getQaTeamId() {
		return qaTeamId;
	}
	public void setQaTeamId(String qaTeamId) {
		this.qaTeamId = qaTeamId;
	}
	public String getHospitalMemo() {
		return hospitalMemo;
	}
	public void setHospitalMemo(String hospitalMemo) {
		this.hospitalMemo = hospitalMemo;
	}
	public String getQaMemo() {
		return qaMemo;
	}
	public void setQaMemo(String qaMemo) {
		this.qaMemo = qaMemo;
	}
	public String getAdminMemo() {
		return adminMemo;
	}
	public void setAdminMemo(String adminMemo) {
		this.adminMemo = adminMemo;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getDocumentState() {
		return documentState;
	}
	public void setDocumentState(String documentState) {
		this.documentState = documentState;
	}
	public String getOperateStartTime() {
		return operateStartTime;
	}
	public void setOperateStartTime(String operateStartTime) {
		this.operateStartTime = operateStartTime;
	}
	public String getOperateEndTime() {
		return operateEndTime;
	}
	public void setOperateEndTime(String operateEndTime) {
		this.operateEndTime = operateEndTime;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public Integer getHospitalEvaluate() {
		return hospitalEvaluate;
	}
	public void setHospitalEvaluate(Integer hospitalEvaluate) {
		this.hospitalEvaluate = hospitalEvaluate;
	}
	public String getHospitalEvaluateMemo() {
		return hospitalEvaluateMemo;
	}
	public void setHospitalEvaluateMemo(String hospitalEvaluateMemo) {
		this.hospitalEvaluateMemo = hospitalEvaluateMemo;
	}
	public Integer getDoctorEvaluate() {
		return doctorEvaluate;
	}
	public void setDoctorEvaluate(Integer doctorEvaluate) {
		this.doctorEvaluate = doctorEvaluate;
	}
	public String getDoctorEvaluateMemo() {
		return doctorEvaluateMemo;
	}
	public void setDoctorEvaluateMemo(String doctorEvaluateMemo) {
		this.doctorEvaluateMemo = doctorEvaluateMemo;
	}
	public String getPatientSex() {
		return patientSex;
	}
	public void setPatientSex(String patientSex) {
		this.patientSex = patientSex;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getOperativeName() {
		return operativeName;
	}
	public void setOperativeName(String operativeName) {
		this.operativeName = operativeName;
	}
	public String getAnestheticName() {
		return anestheticName;
	}
	public void setAnestheticName(String anestheticName) {
		this.anestheticName = anestheticName;
	}

}
