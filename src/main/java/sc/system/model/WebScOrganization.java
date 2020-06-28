package sc.system.model;

public class WebScOrganization {
	private String orgId;
	private String orgPid;
	private String orgName;
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
	private String orgParentId;
	
	public String getOrgId() {
		return orgId;
	}
	
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	
	public String getOrgPid() {
		return orgPid;
	}
	
	public void setOrgPid(String orgPid) {
		this.orgPid = orgPid;
	}
	
	public String getOrgName() {
		return orgName;
	}
	
	public void setOrgName(String orgName) {
		this.orgName = orgName;
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
	
	public String getOrgParentId() {
		return orgParentId;
	}
	
	public void setOrgParentId(String orgParentId) {
		this.orgParentId = orgParentId;
	}
	
	@Override
	public String toString() {
		return "WebScOrganization [orgId=" + orgId + ", orgPid=" + orgPid + ", orgName=" + orgName + ", orgAddress="
				+ orgAddress + ", orgTel=" + orgTel + ", leaderName=" + leaderName + ", leaderTel=" + leaderTel
				+ ", province=" + province + ", city=" + city + ", area=" + area + ", orgParentId=" + orgParentId + "]";
	}
	
}
