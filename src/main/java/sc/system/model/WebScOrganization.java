package sc.system.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 手术类型实体bean
 * @author aisino
 *
 */
public class WebScOrganization {
	
	@JsonProperty("id")
	private String orgId;
	
	@JsonProperty("name")
	private String orgName;
	
	@JsonProperty("parentId")
	private String orgPid;
	
	private String rootId;
	private String rootName;
	private String credentials;
	private String narcoticCard;
	private int leaf;
	
	private String orgAddress;
	private String orgTel;
	private String leaderName;
	private String leaderTel;
	private String province;
	private String provinceName;
	private String city;
	private String cityName;
	private String area;
	private String areaName;
	private String status;
	private float avgEvaluate;
//	private String porgName;
//	private String orgParentId;
	private boolean disabled = false;	// 用于前端当前节点是否不可选中，默认fasle：可选中
	private List<WebScOrganization> children;
	
	public String getOrgId() {
		return orgId;
	}
	
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	public String getOrgName() {
		return orgName;
	}
	
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	public String getOrgPid() {
		return orgPid;
	}
	
	public void setOrgPid(String orgPid) {
		this.orgPid = orgPid;
	}
	
	public String getRootId() {
		return rootId;
	}
	
	public void setRootId(String rootId) {
		this.rootId = rootId;
	}
	
	public String getRootName() {
		return rootName;
	}
	
	public void setRootName(String rootName) {
		this.rootName = rootName;
	}
	
	public String getCredentials() {
		return credentials;
	}
	
	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}
	
	public String getNarcoticCard() {
		return narcoticCard;
	}
	
	public void setNarcoticCard(String narcoticCard) {
		this.narcoticCard = narcoticCard;
	}
	
	public int getLeaf() {
		return leaf;
	}
	
	public void setLeaf(int leaf) {
		this.leaf = leaf;
	}
	
	public String getOrgAddress() {
		return orgAddress;
	}
	
	public void setOrgAddress(String orgAddress) {
		this.orgAddress = orgAddress;
	}
	
	public String getOrgTel() {
		return orgTel;
	}
	
	public void setOrgTel(String orgTel) {
		this.orgTel = orgTel;
	}
	
	public String getLeaderName() {
		return leaderName;
	}
	
	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
	}
	
	public String getLeaderTel() {
		return leaderTel;
	}
	
	public void setLeaderTel(String leaderTel) {
		this.leaderTel = leaderTel;
	}
	
	public String getProvince() {
		return province;
	}
	
	public void setProvince(String province) {
		this.province = province;
	}
	
	public String getProvinceName() {
		return provinceName;
	}
	
	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getCityName() {
		return cityName;
	}
	
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public String getArea() {
		return area;
	}
	
	public void setArea(String area) {
		this.area = area;
	}
	
	public String getAreaName() {
		return areaName;
	}
	
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
//	public String getOrgParentId() {
//		return orgParentId;
//	}
//	
//	public void setOrgParentId(String orgParentId) {
//		this.orgParentId = orgParentId;
//	}
	
//	public String getPorgName() {
//		return porgName;
//	}
//	
//	public void setPorgName(String porgName) {
//		this.porgName = porgName;
//	}
	
	public List<WebScOrganization> getChildren() {
		return children;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
	
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public void setChildren(List<WebScOrganization> children) {
		this.children = children;
	}
	
	public float getAvgEvaluate() {
		return avgEvaluate;
	}

	public void setAvgEvaluate(float avgEvaluate) {
		this.avgEvaluate = avgEvaluate;
	}

	@Override
	public String toString() {
		return "WebScOrganization [orgId=" + orgId + ", orgName=" + orgName + ", orgPid=" + orgPid + ", rootId="
				+ rootId + ", rootName=" + rootName + ", credentials=" + credentials + ", narcoticCard=" + narcoticCard
				+ ", leaf=" + leaf + ", orgAddress=" + orgAddress + ", orgTel=" + orgTel + ", leaderName=" + leaderName
				+ ", leaderTel=" + leaderTel + ", province=" + province + ", provinceName=" + provinceName + ", city="
				+ city + ", cityName=" + cityName + ", area=" + area + ", areaName=" + areaName + ", status=" + status
				+ ", avgEvaluate=" + avgEvaluate + ", disabled=" + disabled + ", children=" + children + "]";
	}

}
